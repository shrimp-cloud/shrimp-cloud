package com.wkclz.mybatis.bean;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.tools.Md5Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataSourceInfo {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceInfo.class);

    private static final Map<String, DruidDataSource> DS_MAP = new HashMap<>();

    private String url;
    private String driverClassName = "com.mysql.cj.jdbc.Driver";
    private String username;
    private String password;

    /**
     * 取得已经构造生成的数据库连接
     */
    public static DruidPooledConnection getConnect(DataSourceInfo dataSourceInfo) {
        DruidDataSource druidDataSource = getDruidDataSource(dataSourceInfo);
        try {
            return druidDataSource.getConnection();
        } catch (SQLException e) {
            throw BizException.error("can not get conn from {}, err: {}", dataSourceInfo.getUrl(), e.getMessage());
        }
    }

    public static DruidDataSource getDruidDataSource(String url, String username, String password) {
        DataSourceInfo info = new DataSourceInfo();
        info.setUrl(url);
        info.setUsername(username);
        info.setPassword(password);
        return getDruidDataSource(info);
    }


    /**
     * 建立连接池
     * TODO 同地址，同数据库名，若其他信息变更，需要销毁连接池
     * 简单方案: 为 DS_MAP 设置过期时间，时间到了做销毁动作
     * 复杂方案: 数据源变更时，主动取出此连接池做销毁动作
     */
    public static DruidDataSource getDruidDataSource(DataSourceInfo dataSourceInfo) {

        String key = Md5Tool.md5(dataSourceInfo.getUrl() + "_" +
            dataSourceInfo.getUsername() + "_" + dataSourceInfo.getPassword());

        DruidDataSource ds = DS_MAP.get(key);
        if (ds != null) {
            return ds;
        }

        synchronized (DataSourceInfo.class) {
            ds = DS_MAP.get(key);
            if (ds != null) {
                return ds;
            }
            ds = new DruidDataSource();

            //设置连接参数
            ds.setUrl(dataSourceInfo.getUrl());
            ds.setDriverClassName(dataSourceInfo.getDriverClassName());
            ds.setUsername(dataSourceInfo.getUsername());
            ds.setPassword(dataSourceInfo.getPassword());
            //配置初始化大小、最小、最大
            ds.setInitialSize(1);
            ds.setMinIdle(1);
            ds.setMaxActive(20);
            //连接泄漏监测
            ds.setRemoveAbandoned(true);
            ds.setRemoveAbandonedTimeout(30);
            //配置获取连接等待超时的时间
            ds.setMaxWait(20000);
            //配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
            ds.setTimeBetweenEvictionRunsMillis(20000);
            //防止过期
            ds.setValidationQuery("SELECT 'x'");
            ds.setTestWhileIdle(true);
            ds.setTestOnBorrow(true);
            ds.setBreakAfterAcquireFailure(true);
            ds.setConnectionErrorRetryAttempts(0);

            DS_MAP.put(key, ds);
        }
        return DS_MAP.get(key);
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