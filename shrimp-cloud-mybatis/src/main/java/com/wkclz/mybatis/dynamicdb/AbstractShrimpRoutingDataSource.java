package com.wkclz.mybatis.dynamicdb;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;


/**
 * 当前类，在全部 Override 父类之外，新增了：
 * addDataSource: 向数据源集合中添加新的数据源
 * getDataSource: 通过 lookupKey 获取数据源
 */
public abstract class AbstractShrimpRoutingDataSource extends AbstractRoutingDataSource {

    // 目标多数据源集合
    @Nullable
    private Map<Object, Object> targetDataSources;
    // 默认数据源对象
    @Nullable
    private Object defaultTargetDataSource;
    // 通过JNDI查找数据源，如果数据源不存在是否回滚到默认数据源，默认：true
    private boolean lenientFallback = true;
    // 通过JNDI查找多数据源对象默认实现类
    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
    // targetDataSources 数据源集合的解析后的key-value对象
    @Nullable
    private Map<Object, DataSource> resolvedDataSources;
    // 解析后的默认数据源对象
    @Nullable
    private DataSource resolvedDefaultDataSource;


    public void addDataSource(Object lookupKey, DataSource dataSource) {
        Assert.notNull(resolvedDataSources, "DataSource router not initialized");
        Assert.notNull(lookupKey, "router lookupKey can't be null");
        resolvedDataSources.put(lookupKey, dataSource);
    }

    public DataSource getDataSource(Object lookupKey) {
        Assert.notNull(resolvedDataSources, "DataSource router not initialized");
        Assert.notNull(lookupKey, "router lookupKey can't be null");
        return resolvedDataSources.get(lookupKey);
    }

    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
    }

    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    public void setDataSourceLookup(@Nullable DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
    }

    public void afterPropertiesSet() {
        if (this.targetDataSources == null) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        }
        this.resolvedDataSources = CollectionUtils.newHashMap(this.targetDataSources.size());
        this.targetDataSources.forEach((key, value) -> {
            Object lookupKey = resolveSpecifiedLookupKey(key);
            DataSource dataSource = resolveSpecifiedDataSource(value);
            this.resolvedDataSources.put(lookupKey, dataSource);
        });
        if (this.defaultTargetDataSource != null) {
            this.resolvedDefaultDataSource = resolveSpecifiedDataSource(this.defaultTargetDataSource);
        }
    }

    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }

    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSource) {
            return (DataSource) dataSource;
        } else if (dataSource instanceof String) {
            return this.dataSourceLookup.getDataSource((String) dataSource);
        } else {
            throw new IllegalArgumentException(
                "Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
        }
    }

    public Map<Object, DataSource> getResolvedDataSources() {
        Assert.state(this.resolvedDataSources != null, "DataSources not resolved yet - call afterPropertiesSet");
        return Collections.unmodifiableMap(this.resolvedDataSources);
    }

    @Nullable
    public DataSource getResolvedDefaultDataSource() {
        return this.resolvedDefaultDataSource;
    }

    public Connection getConnection() throws SQLException {
        return determineTargetDataSource().getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDataSource().unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
    }

    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
        Object lookupKey = determineCurrentLookupKey();
        DataSource dataSource = this.resolvedDataSources.get(lookupKey);
        if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
            dataSource = this.resolvedDefaultDataSource;
        }
        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        return dataSource;
    }

    protected abstract Object determineCurrentLookupKey();

}
