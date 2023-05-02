package com.wkclz.mybatis.bean;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.SecretUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataSourceInfo {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceInfo.class);


    private static Map<String, DruidPooledConnection> dataConns = null;


    private String url;

    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    private String username;

    private String password;


    /**
     * 取得已经构造生成的数据库连接
     *
     * @return 返回数据库连接对象
     * @throws Exception
     */
    public static synchronized DruidPooledConnection getConnect(DataSourceInfo dataSourceInfo) {

        if (dataConns == null) {
            dataConns = new HashMap<>();
        }
        String url = dataSourceInfo.getUrl();
        String hex = SecretUtil.md5(url);
        DruidPooledConnection conn = dataConns.get(hex);
        try {

            if (conn != null && !conn.isClosed() && !conn.isAbandonded()){
                return conn;
            }

            DruidDataSource druidDataSource = getDruidDataSource(dataSourceInfo);
            conn = druidDataSource.getConnection();
            dataConns.put(hex, conn);
            return conn;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw BizException.error("can not get conn from {}, err: {}", url, e.getMessage());
        }
    }

    public static DruidDataSource getDruidDataSource(String url, String username, String password) {
        DataSourceInfo info = new DataSourceInfo();
        info.setUrl(url);
        info.setUsername(username);
        info.setPassword(password);
        return getDruidDataSource(info);
    }


    public static DruidDataSource getDruidDataSource(DataSourceInfo dataSourceInfo) {

        DruidDataSource db = new DruidDataSource();

        //设置连接参数
        db.setUrl(dataSourceInfo.getUrl());
        db.setDriverClassName(dataSourceInfo.getDriverClassName());
        db.setUsername(dataSourceInfo.getUsername());
        db.setPassword(dataSourceInfo.getPassword());
        //配置初始化大小、最小、最大
        db.setInitialSize(1);
        db.setMinIdle(1);
        db.setMaxActive(20);
        //连接泄漏监测
        db.setRemoveAbandoned(true);
        db.setRemoveAbandonedTimeout(30);
        //配置获取连接等待超时的时间
        db.setMaxWait(20000);
        //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        db.setTimeBetweenEvictionRunsMillis(20000);
        //防止过期
        db.setValidationQuery("SELECT 'x'");
        db.setTestWhileIdle(true);
        db.setTestOnBorrow(true);
        db.setBreakAfterAcquireFailure(true);
        db.setConnectionErrorRetryAttempts(0);

        return db;
    }


    public static Map<String, DruidPooledConnection> getDataConns() {
        return dataConns;
    }

    public static void setDataConns(Map<String, DruidPooledConnection> dataConns) {
        DataSourceInfo.dataConns = dataConns;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}