package com.wkclz.mybatis.dynamicdb;

import javax.sql.DataSource;

public interface DynamicDataSourceFactory {

    /**
     * 加载默认数据源
     */
    DataSource createDefaultDataSource();

    /**
     * 使用 key 加载自定义数据源
     */
    DataSource createDataSource(String key);

}
