package com.wkclz.mybatis.plugins.dbinit;

import com.wkclz.mybatis.bean.ColumnInfo;
import com.wkclz.mybatis.bean.IndexInfo;
import com.wkclz.mybatis.bean.SqlScriptInfo;
import com.wkclz.mybatis.bean.TableInfo;
import com.wkclz.mybatis.config.DbScriptInitConfig;
import com.wkclz.spring.helper.IpHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author shrimp
 */
@Slf4j
@Component
public class DatabaseInitProcessor {
    // 当前实例的唯一标识
    private static String INSTANCE_ID = null;

    @Resource
    private DataSource dataSource;
    @Resource
    private SqlSession sqlSession;
    @Resource
    private DbScriptInitConfig dbScriptInitConfig;
    @Resource
    private DatabaseUpdateHelper databaseUpdateHelper;

    public void dbinit() throws BeansException {
        // 总开关
        Integer enabled = dbScriptInitConfig.getEnabled();
        if (enabled == null || enabled != 1) {
            return;
        }

        // 使用数据库锁避免多实例重复执行
        Connection connection = null;
        boolean lockAcquired = false;
        try {
            connection = dataSource.getConnection();
            lockAcquired = acquireDistributedLock(connection);

            if (!lockAcquired) {
                log.info("未能获取数据库初始化锁，可能其他实例正在执行初始化");
                return;
            }

            log.info("成功获取数据库初始化锁，实例ID: {}，开始执行数据库初始化", getInstanceId());

            // 获取所有 DDL 脚本
            List<SqlScriptInfo> scripts = DatabaseUpdateHelper.scanSqlScripts();
            if (scripts.isEmpty()) {
                log.debug("tables can not be scan!");
                return;
            }

            // 获取已存在的表信息
            List<SqlScriptInfo> tables = scripts.stream().filter(t -> "ddl".equals(t.getType())).toList();
            List<String> tableNames = tables.stream().map(SqlScriptInfo::getTableName).toList();
            List<TableInfo> dbexistTables = databaseUpdateHelper.getTables(tableNames);

            // 新出现的表
            List<String> existTableNames = dbexistTables.stream().map(TableInfo::getTableName).toList();
            List<SqlScriptInfo> newTables = tables.stream().filter(t -> !existTableNames.contains(t.getTableName())).toList();
            List<String> newTableNames = newTables.stream().map(SqlScriptInfo::getTableName).toList();

            // 新表中的数据
            List<SqlScriptInfo> dmls = scripts.stream()
                .filter(t -> "dml".equals(t.getType()))
                .filter(t -> newTableNames.contains(t.getTableName()))
                .toList();

            // 字段处理  existTables ->
            List<SqlScriptInfo> scriptExistTables = tables.stream().filter(t -> existTableNames.contains(t.getTableName())).toList();

            // scriptExistTables 与 dbeExistTables 对比，得到  alter 和 drop 语句
            List<String> statements = handleTable4Alter(scriptExistTables, dbexistTables);

            // alter 处理分类
            List<String> addColumns = statements.stream().filter(t -> t.contains(" ADD COLUMN ")).toList();
            List<String> modifyColumns = statements.stream().filter(t -> t.contains(" MODIFY COLUMN ")).toList();
            List<String> dropColumns = statements.stream().filter(t -> t.contains(" DROP COLUMN ")).toList();
            List<String> addIndexes = statements.stream().filter(t -> t.contains(" ADD ") && t.contains(" INDEX ")).toList();
            List<String> dropIndexes = statements.stream().filter(t -> t.contains(" DROP INDEX ")).toList();

            log.info("init tables and data...");

            // 创建表
            executeCreateTables(newTables);
            // 新表插入新数据
            executeInsertDatas(dmls);

            // 字段，索引处理
            executeAlterAddColumns(addColumns);
            executeAlterModifyColumns(modifyColumns);
            executeAlterDropColumns(dropColumns);
            executeAlterAddIndexes(addIndexes);
            executeAlterDropIndexes(dropIndexes);

            log.info("init tables and data success!");
        } catch (Exception e) {
            log.error("数据库初始化过程中发生异常", e);
        } finally {
            // 释放锁
            if (lockAcquired && connection != null) {
                try {
                    releaseDistributedLock(connection);
                } catch (Exception e) {
                    log.error("释放数据库初始化锁时发生异常", e);
                }
            }

            // 关闭连接
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    log.error("关闭数据库连接时发生异常", e);
                }
            }
        }
        return;
    }

    /**
     * 获取分布式锁
     *
     * @param connection 数据库连接
     * @return 是否成功获取锁
     */
    private boolean acquireDistributedLock(Connection connection) throws SQLException {
        try {
            // 创建锁表（如果不存在）
            createLockTableIfNotExists(connection);

            // 清理过期锁
            cleanupExpiredLocks(connection);

            // 尝试插入锁记录
            String sql = "INSERT INTO db_init_lock (lock_key, lock_owner, create_time) VALUES (?, ?, NOW())";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, "db_init_lock");
                ps.setString(2, getInstanceId());
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            // 如果是唯一约束冲突，说明锁已被其他实例持有
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("UNIQUE constraint failed")) {
                return false;
            }
            throw e;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param connection 数据库连接
     */
    private void releaseDistributedLock(Connection connection) throws SQLException {
        String sql = "DELETE FROM db_init_lock WHERE lock_key = ? AND lock_owner = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "db_init_lock");
            ps.setString(2, getInstanceId());
            ps.executeUpdate();
        }
        log.info("已释放数据库初始化锁，实例ID: {}", getInstanceId());
    }

    /**
     * 创建锁表（如果不存在）
     *
     * @param connection 数据库连接
     */
    private void createLockTableIfNotExists(Connection connection) throws SQLException {
        // 检查表是否已存在
        String checkSql = "SELECT 1 FROM information_schema.TABLES WHERE TABLE_NAME = 'db_init_lock' AND TABLE_SCHEMA = DATABASE()";
        try (PreparedStatement ps = connection.prepareStatement(checkSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return; // 表已存在
            }
        } catch (SQLException e) {
            // 忽略检查表是否存在的异常
        }

        // 创建表
        String createTableSql = """
                    CREATE TABLE db_init_lock (
                    lock_key VARCHAR(100) NOT NULL,
                    lock_owner VARCHAR(100) NOT NULL,
                    create_time DATETIME NOT NULL,
                    PRIMARY KEY (lock_key))
                """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);
            log.info("已创建数据库初始化锁表 db_init_lock");
        } catch (SQLException e) {
            // 可能其他实例已经创建了表，忽略异常
            log.debug("创建锁表时发生异常（可能是并发创建导致）: {}", e.getMessage());
        }
    }

    /**
     * 清理过期锁
     *
     * @param connection 数据库连接
     */
    private void cleanupExpiredLocks(Connection connection) throws SQLException {
        String sql = "DELETE FROM db_init_lock WHERE create_time < DATE_SUB(NOW(), INTERVAL 5 MINUTE)";
        try (Statement stmt = connection.createStatement()) {
            int count = stmt.executeUpdate(sql);
            if (count > 0) {
                log.info("清理了 {} 个过期的数据库初始化锁", count);
            }
        }
    }


    private synchronized String getInstanceId() {
        if (INSTANCE_ID == null) {
            INSTANCE_ID = IpHelper.getServerIp();
        }
        return INSTANCE_ID;
    }


    /**
     * 处理表结构变更
     * @param scriptTables 脚本中存在的表
     * @param dbTables 数据库中实际存在的表
     */
    private List<String> handleTable4Alter(List<SqlScriptInfo> scriptTables, List<TableInfo> dbTables) {
        // 创建一个映射，方便通过表名查找TableInfo
        Map<String, TableInfo> dbTableMap = dbTables.stream()
                .collect(Collectors.toMap(TableInfo::getTableName, t -> t));

        List<String> alters = new ArrayList<>();
        // 检查现有表结构是否需要变更
        for (SqlScriptInfo scriptTable : scriptTables) {
            String tableName = scriptTable.getTableName();
            TableInfo dbTable = dbTableMap.get(tableName);
            if (dbTable != null && scriptTable.getTableInfo() != null) {
                // 对比表结构，生成ALTER语句
                List<String> alterStatements = compareTableStructure(dbTable, scriptTable.getTableInfo());
                if (!alterStatements.isEmpty()) {
                    log.info("Table {} requires {} schema changes", tableName, alterStatements.size());
                    alters.addAll(alterStatements);
                }
                
                // 对比索引结构，生成索引ALTER语句
                List<String> indexAlterStatements = compareIndexStructure(dbTable, scriptTable.getTableInfo());
                if (!indexAlterStatements.isEmpty()) {
                    log.info("Table {} requires {} index changes", tableName, indexAlterStatements.size());
                    alters.addAll(indexAlterStatements);
                }
            }
        }
        return alters;
    }

    /**
     * 对比表索引差异
     * @param existTable 数据库中现有的表索引
     * @param scriptTable 脚本中的表索引
     * @return ALTER语句列表
     */
    private static List<String> compareIndexStructure(TableInfo existTable, TableInfo scriptTable) {
        List<String> alterStatements = new ArrayList<>();
        
        // 创建现有索引的映射（按索引名分组）
        Map<String, IndexInfo> existIndexes = existTable.getIndexs().stream().collect(Collectors.toMap(IndexInfo::getIndexName, t -> t));

        // 创建脚本索引的映射（按索引名分组）
        Map<String, IndexInfo> scriptIndexes = scriptTable.getIndexs().stream().collect(Collectors.toMap(IndexInfo::getIndexName, t -> t));

        
        // 检查新增或修改的索引
        for (Map.Entry<String, IndexInfo> scriptEntry : scriptIndexes.entrySet()) {
            String indexName = scriptEntry.getKey();
            IndexInfo scriptIndexColumn = scriptEntry.getValue();
            
            // 主键索引特殊处理，这里我们只处理普通索引和唯一索引
            if ("PRIMARY".equals(indexName)) {
                continue;
            }
            
            IndexInfo existIndexColumns = existIndexes.get(indexName);
            
            if (existIndexColumns == null) {
                // 新增索引
                String addIndexSql = DatabaseUpdateHelper.generateAddIndexSql(scriptIndexColumn);
                alterStatements.add(addIndexSql);
            } else {
                // 检查索引是否需要修改（比较列是否一致）
                if (!areIndexesEqual(existIndexColumns, scriptIndexColumn)) {
                    // 先删除再添加索引
                    String dropIndexSql = DatabaseUpdateHelper.generateDropIndexSql(scriptTable.getTableName(), indexName);
                    String addIndexSql = DatabaseUpdateHelper.generateAddIndexSql(scriptIndexColumn);
                    alterStatements.add(dropIndexSql);
                    alterStatements.add(addIndexSql);
                }
            }
        }

        // 检查需要删除的索引（在现有表中存在但在脚本中不存在）
        for (Map.Entry<String, IndexInfo> existEntry : existIndexes.entrySet()) {
            String indexName = existEntry.getKey();
            
            // 主键索引特殊处理，这里我们只处理普通索引和唯一索引
            if ("PRIMARY".equals(indexName)) {
                continue;
            }
            if (!scriptIndexes.containsKey(indexName)) {
                // 删除索引
                String dropIndexSql = DatabaseUpdateHelper.generateDropIndexSql(scriptTable.getTableName(), indexName);
                alterStatements.add(dropIndexSql);
            }
        }
        return alterStatements;
    }
    
    /**
     * 判断两个索引定义是否相等
     * @param existIndex 现有索引列信息
     * @param scriptIndex 脚本索引列信息
     * @return 是否相等
     */
    private static boolean areIndexesEqual(IndexInfo existIndex, IndexInfo scriptIndex) {
        // 比较索引列数量
        if (existIndex.getColumns().size() != scriptIndex.getColumns().size()) {
            return false;
        }
        
        // 比较索引是否都是唯一索引
        Boolean existNonUnique = existIndex.getNonUnique() == 1;
        Boolean scriptNonUnique = scriptIndex.getNonUnique() == 1;
        if (!Objects.equals(existNonUnique, scriptNonUnique)) {
            return false;
        }
        
        // 比较索引列名称顺序
        List<String> existColumnNames = existIndex.getColumns();
        List<String> scriptColumnNames = scriptIndex.getColumns();
        return Objects.equals(existColumnNames, scriptColumnNames);
    }

    /**
     * 对比表结构差异
     * @param existTable 数据库中现有的表结构
     * @param scriptTable 脚本中的表结构
     * @return ALTER语句列表
     */
    private static List<String> compareTableStructure(TableInfo existTable, TableInfo scriptTable) {
        List<String> alterStatements = new ArrayList<>();
        
        // 创建现有列的映射
        Map<String, ColumnInfo> existColumns = existTable.getColumns().stream()
                .collect(Collectors.toMap(ColumnInfo::getColumnName, c -> c));
        
        // 创建脚本列的映射
        Map<String, ColumnInfo> scriptColumns = scriptTable.getColumns().stream()
                .collect(Collectors.toMap(ColumnInfo::getColumnName, c -> c));
        
        // 检查新增或修改的列
        for (ColumnInfo scriptColumn : scriptTable.getColumns()) {
            String columnName = scriptColumn.getColumnName();
            ColumnInfo existColumn = existColumns.get(columnName);
            
            if (existColumn == null) {
                // 新增列
                String addColumnSql = DatabaseUpdateHelper.generateAddColumnSql(scriptColumn);
                alterStatements.add(addColumnSql);
            } else {
                // 检查列是否需要修改
                if (!areColumnsEqual(existColumn, scriptColumn)) {
                    String modifyColumnSql = DatabaseUpdateHelper.generateModifyColumnSql(scriptColumn);
                    alterStatements.add(modifyColumnSql);
                }
            }
        }

        // 检查需要删除的列（在现有表中存在但在脚本中不存在）
        for (ColumnInfo existColumn : existTable.getColumns()) {
            String columnName = existColumn.getColumnName();
            if (!scriptColumns.containsKey(columnName)) {
                // 删除列
                String dropColumnSql = DatabaseUpdateHelper.generateDropColumnSql(scriptTable.getTableName(), columnName);
                alterStatements.add(dropColumnSql);
            }
        }
        return alterStatements;
    }
    
    /**
     * 判断两个列定义是否相等
     * @param existColumn 现有列
     * @param scriptColumn 脚本列
     * @return 是否相等
     */
    private static boolean areColumnsEqual(ColumnInfo existColumn, ColumnInfo scriptColumn) {
        // 比较数据类型
        if (!Objects.equals(existColumn.getDataType(), scriptColumn.getDataType())) {
            return false;
        }
        
        // 比较长度（对于有长度限制的类型）
        if (!Objects.equals(existColumn.getLength(), scriptColumn.getLength())) {
            return false;
        }
        
        // 比较是否可为空
        if (!existColumn.getNotNull().equals(scriptColumn.getNotNull())) {
            return false;
        }
        
        // 比较默认值
        Object a = existColumn.getDefaultValue();
        Object b = scriptColumn.getDefaultValue();
        if ( (a == null && b != null ) || (a != null && b == null ) || (a != null && b != null && !a.toString().equals(b.toString())) ) {
            return false;
        }
        
        // 比较注释
        if (!Objects.equals(existColumn.getColumnComment(), scriptColumn.getColumnComment())) {
            return false;
        }
        
        // 比较是否为自增列
        if (!existColumn.getAutoIncrement().equals(scriptColumn.getAutoIncrement())) {
            return false;
        }
        
        return true;
    }


    private void executeAlterAddColumns(List<String> addColumns) {
        Integer b = dbScriptInitConfig.getAutoExecAddColumn();
        if (b != null && b == 1) {
            DatabaseUpdateHelper.executeStatement(sqlSession, addColumns);
        } else {
            DatabaseUpdateHelper.printSql(addColumns);
        }
    }

    private void executeAlterModifyColumns(List<String> modifyColumns) {
        Integer b = dbScriptInitConfig.getAutoExecModifyColumn();
        if (b != null && b == 1) {
            DatabaseUpdateHelper.executeStatement(sqlSession, modifyColumns);
        } else {
            DatabaseUpdateHelper.printSql(modifyColumns);
        }
    }

    private void executeAlterDropColumns(List<String> dropColumns) {
        Integer b = dbScriptInitConfig.getAutoExecDropColumn();
        if (b != null && b == 1) {
            DatabaseUpdateHelper.executeStatement(sqlSession, dropColumns);
        } else {
            DatabaseUpdateHelper.printSql(dropColumns);
        }
    }

    private void executeAlterAddIndexes(List<String> addIndexes) {
        Integer b = dbScriptInitConfig.getAutoExecAddIndex();
        if (b != null && b == 1) {
            DatabaseUpdateHelper.executeStatement(sqlSession, addIndexes);
        } else {
            DatabaseUpdateHelper.printSql(addIndexes);
        }
    }

    private void executeAlterDropIndexes(List<String> dropIndexes) {
        Integer b = dbScriptInitConfig.getAutoExecDropIndex();
        if (b != null && b == 1) {
            DatabaseUpdateHelper.executeStatement(sqlSession, dropIndexes);
        } else {
            DatabaseUpdateHelper.printSql(dropIndexes);
        }
    }

    private void executeCreateTables(List<SqlScriptInfo> infos) {
        Integer b = dbScriptInitConfig.getAutoCreateTable();
        List<String> sqls = infos.stream().map(SqlScriptInfo::getScript).toList();
        if (b != null && b == 1) {
            DatabaseUpdateHelper.executeStatement(sqlSession, sqls);
        } else {
            DatabaseUpdateHelper.printSql(sqls);
        }
    }

    private void executeInsertDatas(List<SqlScriptInfo> infos) {
        List<String> sqls = infos.stream().map(SqlScriptInfo::getScript).toList();
        Integer b = dbScriptInitConfig.getAutoInsertData();
        if (b != null && b == 1) {
            DatabaseUpdateHelper.executeStatement(sqlSession, sqls);
        } else {
            DatabaseUpdateHelper.printSql(sqls);
        }
    }

}