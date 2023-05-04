package com.wkclz.mybatis.dynamicdb;

/**
 * 实现对数据源的操作功能
 */
public class DynamicDataSourceHolder {

    private static final ThreadLocal<String> dataSourceHolder = new ThreadLocal<>();

    public static void set(String key) {
        dataSourceHolder.set(key);
    }

    public static String get() {
        return dataSourceHolder.get();
    }

    public static void clear() {
        dataSourceHolder.remove();
    }

}
