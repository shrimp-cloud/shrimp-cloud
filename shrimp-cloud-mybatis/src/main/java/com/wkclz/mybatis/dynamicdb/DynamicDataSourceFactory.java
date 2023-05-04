package com.wkclz.mybatis.dynamicdb;

import javax.sql.DataSource;

public interface DynamicDataSourceFactory {

    /**
     * 使用 key 加载自定义数据源
     */
    DataSource createDataSource(String key);

}
