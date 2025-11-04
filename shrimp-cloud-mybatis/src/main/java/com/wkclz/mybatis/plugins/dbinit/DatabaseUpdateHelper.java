package com.wkclz.mybatis.plugins.dbinit;

import com.wkclz.common.exception.SysException;
import com.wkclz.mybatis.bean.*;
import com.wkclz.mybatis.dao.TableInfoMapper;
import com.wkclz.mybatis.helper.SqlHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author shrimp
 */

@Slf4j
@Component
public class DatabaseUpdateHelper {


    @Resource
    private TableInfoMapper tableInfoMapper;
    @Resource
    private DataSource dataSource;

    // 扫描所有脚本文件
    public static List<SqlScriptInfo> scanSqlScripts() {
        // 读取所有文件
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] resources = null;
        try {
            resources = resolver.getResources("classpath*:sql-script/*.sql");
        } catch (IOException e) {
            throw SysException.error("无法扫描 sql-script 脚本信息: {}", e.getMessage());
        }
        List<SqlScriptInfo> infos = new ArrayList<>();

        for (org.springframework.core.io.Resource resource : resources) {
            try (InputStream inputStream = resource.getInputStream()) {
                String script = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                String[] splitStrs = script.split(";");
                for (String split : splitStrs) {
                    split = SqlHelper.removeComments(split);
                    split = split.replaceAll("\\s+", " ").trim();
                    if (split.isEmpty()) {
                        continue;
                    }

                    SqlScriptInfo info = new SqlScriptInfo();
                    info.setFileName(resource.getFilename());
                    info.setScript(split);

                    // DML
                    if (split.startsWith("INSERT") || split.startsWith("insert")) {
                        info.setType("dml");
                        UpdateInfo updateInfo = SqlHelper.getUpdateInfo(split);
                        info.setTableName(updateInfo.getTableName());
                        info.setUpdateInfo(updateInfo);
                    }

                    // DDL
                    if (split.startsWith("CREATE TABLE") || split.startsWith("create table")) {
                        info.setType("ddl");
                        try {
                            TableInfo tableInfo = SqlHelper.getTableInfo(split);
                            formatColumns(tableInfo.getColumns());

                            info.setTableInfo(tableInfo);
                            info.setTableName(tableInfo.getTableName());
                        } catch (Exception e) {
                            log.warn("{} is not a valid DDL script!", split);
                        }
                    }
                    infos.add(info);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return infos;
    }


    // 获取表清单
    public List<TableInfo> getTables(List<String> tablenames) {
        TableInfo ti = new TableInfo();
        ti.setTableSchema(getTableSchema());
        ti.setTableNames(tablenames);
        List<TableInfo> tables = tableInfoMapper.getTables(ti);
        List<ColumnInfo> columns = tableInfoMapper.getColumns(ti);
        List<IndexInfo> indexs = tableInfoMapper.getIndexs(ti);

        formatColumns(columns);

        for (TableInfo table : tables) {
            // 字段
            List<ColumnInfo> cs = columns.stream()
                .filter(t -> t.getTableName().equals(table.getTableName()))
                .sorted(Comparator.comparingInt(ColumnInfo::getOrdinalPosition))
                .collect(Collectors.toList());
            for (int i = 1; i < cs.size(); i++) {
                cs.get(i).setAfter(cs.get(i - 1).getColumnName());
            }
            if (!cs.isEmpty()) {
                // 第一个字段设置为FIRST
                cs.getFirst().setOrdinalPosition(0);
            }
            table.setColumns(cs);
            // 索引筛选
            List<IndexInfo> is = indexs.stream()
                .filter(t -> t.getTableName().equals(table.getTableName()))
                .toList();
            // 索引分组
            Map<String, List<IndexInfo>> idxs = is.stream().collect(Collectors.groupingBy(IndexInfo::getIndexName));
            // 索引合并
            List<IndexInfo> mergedIdx = idxs.values().stream().map(ii -> {
                IndexInfo idx = ii.getFirst();
                List<String> cc = ii.stream().map(IndexInfo::getColumnName).collect(Collectors.toList());
                idx.setColumns(cc);
                idx.setColumnName(null);
                return idx;
            }).toList();
            table.setIndexs(mergedIdx);
        }
        return tables;
    }


    // 获取数据库名
    private String getTableSchema() {
        try {
            Connection connection = dataSource.getConnection();
            if (connection.isClosed()) {
                throw new RuntimeException("数据库连接已关闭!");
            }
            String tableSchema = connection.getCatalog();
            if (StringUtils.isNotBlank(tableSchema)) {
                return tableSchema;
            }
            DatabaseMetaData metaData = connection.getMetaData();
            String datasourceUrl = metaData.getURL();
            Pattern pattern = Pattern.compile("jdbc:mysql://[^/]+/([^ ?/]+)");
            Matcher matcher = pattern.matcher(datasourceUrl);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                throw new RuntimeException("未从数据库连接信息中发现数据库名,请联系开发者!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("无法从默认数据源中获取数据库连接信息: " + e.getMessage());
        }
    }


    // 将 ColumnInfo 的内容规范化，格式化
    private static void formatColumns(List<ColumnInfo> columns) {
        if (CollectionUtils.isEmpty(columns)) {
            return;
        }
        for (ColumnInfo column : columns) {
            if (column.getAutoIncrement() == null) {
                column.setAutoIncrement(false);
            }
            if (column.getUnsigned() == null) {
                column.setUnsigned(false);
            }
            if (column.getNotNull() == null) {
                column.setNotNull(false);
            }
            if (column.getOnUpdate() == null) {
                column.setOnUpdate(0);
            }
            if ("text".equals(column.getDataType())) {
                column.setLength(null);
            }
            if ("longtext".equals(column.getDataType())) {
                column.setLength(null);
            }
            if ("int".equals(column.getDataType()) && column.getLength() == null) {
                column.setLength(11L);
            }
            if ("bigint".equals(column.getDataType()) && column.getLength() == null) {
                column.setLength(20L);
            }
        }
    }



    /**
     * 生成添加列的SQL语句
     * @param column 列信息
     * @return ADD COLUMN语句
     */
    public static String generateAddColumnSql(ColumnInfo column) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE `").append(column.getTableName()).append("` ");
        sql.append("ADD COLUMN `").append(column.getColumnName()).append("` ");
        sql.append(column.getDataType());

        // 添加长度（如果有的话）
        if (column.getLength() != null) {
            sql.append("(").append(column.getLength()).append(")");
        }

        // 添加UNSIGNED（如果需要）
        if (column.getUnsigned()) {
            sql.append(" UNSIGNED");
        }

        // 添加是否可为空
        if (column.getNotNull()) {
            sql.append(" NOT NULL");
        }

        // 添加默认值
        if (column.getDefaultValue() != null) {
            sql.append(" DEFAULT '").append(column.getDefaultValue()).append("'");
        }

        // 添加注释
        if (column.getColumnComment() != null) {
            sql.append(" COMMENT '").append(column.getColumnComment()).append("'");
        }

        // 添加自增（如果需要）
        if (column.getAutoIncrement()) {
            sql.append(" AUTO_INCREMENT");
        }
        
        // 添加字段顺序
        if (column.getAfter() != null) {
            sql.append(" AFTER `").append(column.getAfter()).append("`");
        } else if (column.getOrdinalPosition() != null && column.getOrdinalPosition() == 0) {
            sql.append(" FIRST");
        }
        sql.append(";");
        return sql.toString();
    }

    /**
     * 生成修改列的SQL语句
     * @param column 列信息
     * @return MODIFY COLUMN语句
     */
    public static String generateModifyColumnSql(ColumnInfo column) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE `").append(column.getTableName()).append("` ");
        sql.append("MODIFY COLUMN `").append(column.getColumnName()).append("` ");
        sql.append(column.getDataType());

        // 添加长度（如果有的话）
        if (column.getLength() != null) {
            sql.append("(").append(column.getLength()).append(")");
        }

        // 添加UNSIGNED（如果需要）
        if (column.getUnsigned()) {
            sql.append(" UNSIGNED");
        }

        // 添加是否可为空
        if (column.getNotNull()) {
            sql.append(" NOT NULL");
        } else {
            sql.append(" NULL");
        }

        // 添加默认值
        if (column.getDefaultValue() != null) {
            sql.append(" DEFAULT '").append(column.getDefaultValue()).append("'");
        }

        // 添加注释
        if (column.getColumnComment() != null) {
            sql.append(" COMMENT '").append(column.getColumnComment()).append("'");
        }

        // 添加自增（如果需要）
        if (column.getAutoIncrement()) {
            sql.append(" AUTO_INCREMENT");
        }
        
        // 添加字段顺序
        if (column.getAfter() != null) {
            sql.append(" AFTER `").append(column.getAfter()).append("`");
        } else if (column.getOrdinalPosition() != null && column.getOrdinalPosition() == 0) {
            sql.append(" FIRST");
        }
        sql.append(";");
        return sql.toString();
    }

    /**
     * 生成删除列的SQL语句
     * @param tableName 表名
     * @param columnName 列名
     * @return DROP COLUMN语句
     */
    public static String generateDropColumnSql(String tableName, String columnName) {
        return "ALTER TABLE `" + tableName + "` DROP COLUMN `" + columnName + "`";
    }

    /**
     * 生成添加索引的SQL语句
     * @param index 索引列信息
     * @return ADD INDEX语句
     */
    public static String generateAddIndexSql(IndexInfo index) {
        String tableName = index.getTableName();
        String indexName = index.getIndexName();
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE `").append(tableName).append("` ");

        // 判断是否是唯一索引
        Boolean nonUnique = index.getNonUnique() == 1;
        if (!nonUnique) {
            sql.append("ADD UNIQUE INDEX `").append(indexName).append("` ");
        } else {
            sql.append("ADD INDEX `").append(indexName).append("` ");
        }

        // 添加索引列
        sql.append("(");
        sql.append(String.join(",", index.getColumns()));
        sql.append(")");
        if (StringUtils.isNotBlank(index.getType())) {
            sql.append(" USING ").append(index.getType());
        }
        sql.append(";");

        return sql.toString();
    }

    /**
     * 生成删除索引的SQL语句
     * @param tableName 表名
     * @param indexName 索引名
     * @return DROP INDEX语句
     */
    public static String generateDropIndexSql(String tableName, String indexName) {
        return "ALTER TABLE `" + tableName + "` DROP INDEX `" + indexName + "`;";
    }


    public static void executeStatement(SqlSession sqlSession, List<String> sqls) {
        if (CollectionUtils.isEmpty(sqls)) {
            return;
        }
        for (String sql : sqls) {
            executeStatement(sqlSession, sql);
        }
    }

    public static void executeStatement(SqlSession sqlSession, String sql) {
        if (StringUtils.isBlank(sql)) {
            return;
        }
        String md5 = DigestUtils.md5DigestAsHex(sql.getBytes(StandardCharsets.UTF_8));
        String statementId = "dynamic." + md5;
        try {
            Configuration configuration = sqlSession.getConfiguration();
            SqlSource sqlSource = new StaticSqlSource(configuration, sql);
            MappedStatement.Builder statementBuilder = new MappedStatement.Builder(
                configuration,
                statementId,
                sqlSource,
                SqlCommandType.UPDATE
            );
            MappedStatement statement = statementBuilder.build();
            configuration.addMappedStatement(statement);
            sqlSession.update(statementId);
            log.info("Executed execute statement: {}, sql: {}", statementId, sql.replaceAll("\\s+", " "));
        } catch (Exception e) {
            log.error("Failed to execute statement: {}, sql: {}", statementId, sql, e);
        }

    }

    public static void printSql(List<String> sqls) {
        if (CollectionUtils.isEmpty(sqls)) {
            return;
        }
        log.warn("Please execute this script by manual:");
        sqls.forEach(log::warn);
    }

}
