package com.wkclz.mybatis.util;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.MapUtil;
import com.wkclz.mybatis.bean.DataSourceInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class JdbcUtil {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);


    /**
     * SQL 执行查询，指定返回类型
     */
    public static <T> List<T> jdbcExecutor(DataSourceInfo dataSourceInfo, String sql, Class<T> clazz) {
        List<LinkedHashMap> maps = jdbcExecutor(dataSourceInfo, sql);
        List<T> list = MapUtil.map2ObjList(maps, clazz);
        return list;
    }

    /**
     * SQL 执行
     */
    public static List<LinkedHashMap> jdbcExecutor(DataSourceInfo dataSourceInfo, String sql) {
        DruidPooledConnection conn = getConn(dataSourceInfo);

        // SQL 解析，检测
        String dbType = JdbcConstants.MYSQL.name();
        String sqlFormat = SQLUtils.format(sql, dbType);
        logger.info("sql to excute: \n {}", sqlFormat);

        List<SQLStatement> stmtList;
        try {
            stmtList = SQLUtils.parseStatements(sql, dbType);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw BizException.error("can not parse sql: {}", sql);
        }
        if (stmtList.size() > 1) {
            throw BizException.error("can only excute 1 sql at once");
        }
        SQLStatement stmt = stmtList.get(0);
        // 使用visitor来访问AST
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        stmt.accept(visitor);

        // sql 操作类型预判
        boolean select = true;
        for (TableStat tableStat : visitor.getTables().values()) {
            if (tableStat.getSelectCount() == 0) {
                select = false;
                break;
            }
        }

        // sql 执行
        List<LinkedHashMap> maps;
        if (select) {
            maps = jdbcQueryExecutor(conn, sql);
        } else {
            int update = jdbcUpdateExecutor(conn, sql);
            LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
            map.put("rows", update);
            maps = Arrays.asList(map);
        }
        return maps;
    }


    /**
     * 查询类 （SELECT）
     */
    public static List<LinkedHashMap> jdbcQueryExecutor(Connection conn, String sql) {
        try (Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery(sql);
            List<LinkedHashMap> maps = ResultSetMapper.toMapList(results);
            return maps;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw BizException.error("sql error: {}", sql);
        }
    }

    /**
     * 更新类（UPDATE, INSERT, DELETE, SQLDDL）
     */
    public static int jdbcUpdateExecutor(Connection conn, String sql) {
        try (Statement statement = conn.createStatement()) {
            int update = statement.executeUpdate(sql);
            return update;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw BizException.error("sql error: {}", sql);
        }
    }

    private static DruidPooledConnection getConn(DataSourceInfo dataSourceInfo) {
        if (StringUtils.isBlank(dataSourceInfo.getUrl())) {
            throw BizException.error("get conn, url can not be null");
        }
        if (StringUtils.isBlank(dataSourceInfo.getDriverClassName())) {
            throw BizException.error("get conn, driverClass can not be null");
        }
        if (StringUtils.isBlank(dataSourceInfo.getUsername())) {
            throw BizException.error("get username, url can not be null");
        }
        if (StringUtils.isBlank(dataSourceInfo.getPassword())) {
            throw BizException.error("get password, url can not be null");
        }
        return DataSourceInfo.getConnect(dataSourceInfo);

    }


    /**
     * 注入风险检测。默认只作用在 orderBy 上。不能用于其他地方的注入检测
     * 注入风险：
     * 1、orderBy 中使用 ${}。可用此方法进行检测，不可使用其他字段传入
     * 2、MBG 的 noValue，singleValue，betweenValue，listValue 注入风险：Example 的 Criteria 产生，无注入风险
     * 3、MBG 的 like 注入风险：like 前的由 Example 控制，后的为 #{}, 无注入风险
     * 4、Custom 实现的 like 强制使用 AND column like concat("%",#{value},"%")
     *
     * @param str
     * @return
     */
    public static boolean sqlInj(String str) {
        str = str.toLowerCase();
        String injStr = "'|and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;| or |-|+";
        String[] injStra = injStr.split("\\|");
        for (int i = 0; i < injStra.length; i++) {
            String is = injStra[i];
            if (str.indexOf(is) >= 0) {
                return true;
            }
        }
        return false;
    }


}
