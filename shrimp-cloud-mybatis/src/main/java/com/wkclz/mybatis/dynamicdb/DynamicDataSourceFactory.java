package com.wkclz.mybatis.dynamicdb;

import com.wkclz.mybatis.bean.DataSourceInfo;

public interface DynamicDataSourceFactory {

    /**
     * 使用 key 加载自定义数据源
     */
    DataSourceInfo createDataSource(String key);

}
