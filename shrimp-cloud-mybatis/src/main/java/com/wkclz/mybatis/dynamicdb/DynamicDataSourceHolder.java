package com.wkclz.mybatis.dynamicdb;

/**
 * 实现对数据源的操作功能
 */
public class DynamicDataSourceHolder {

    private static final ThreadLocal<String> DATA_SOURCE_HOLDER = new ThreadLocal<>();

    public static void set(String key) {
        DATA_SOURCE_HOLDER.set(key);
    }

    public static String get() {
        return DATA_SOURCE_HOLDER.get();
    }

    public static void clear() {
        DATA_SOURCE_HOLDER.remove();
    }

}
