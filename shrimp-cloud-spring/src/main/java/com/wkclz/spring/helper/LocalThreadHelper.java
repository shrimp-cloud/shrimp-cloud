package com.wkclz.spring.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 支持多 Key 的泛型线程上下文工具类
 * 使用 ThreadLocal + ConcurrentHashMap 实现多 Key 存储
 * 线程安全，适用于 Web 请求上下文传递（如用户信息、traceId 等）
 */
public final class LocalThreadHelper {

    /**
     * 使用 ThreadLocal 存储当前线程的所有上下文数据
     * 内部使用 ConcurrentHashMap 保证线程安全的 Map 操作
     */
    private static final ThreadLocal<Map<String, Object>> contextHolder = ThreadLocal.withInitial(ConcurrentHashMap::new);

    // 工具类，禁止实例化
    private LocalThreadHelper() {}

    /**
     * 设置当前线程中指定 key 的值
     *
     * @param key   键（建议使用常量或枚举）
     * @param value 值（任意类型）
     * @param <T>   值的类型
     */
    public static <T> void set(String key, T value) {
        contextHolder.get().put(key, value);
    }

    /**
     * 获取当前线程中指定 key 的值
     *
     * @param key 键
     * @param <T> 期望的类型
     * @return 对应类型的值，若不存在则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        Object value = contextHolder.get().get(key);
        return (T) value; // 类型安全由调用者保证
    }

    /**
     * 获取当前线程中指定 key 的值，若不存在则使用默认值提供器
     *
     * @param key                  键
     * @param defaultValueSupplier 默认值提供器
     * @param <T>                  值的类型
     * @return 值或默认值
     */
    public static <T> T getOrElse(String key, Supplier<T> defaultValueSupplier) {
        T value = get(key);
        return value != null ? value : defaultValueSupplier.get();
    }

    /**
     * 检查当前线程是否包含指定 key 的值
     *
     * @param key 键
     * @return 是否存在
     */
    public static boolean contains(String key) {
        return contextHolder.get().containsKey(key);
    }

    /**
     * 删除当前线程中指定 key 的值
     *
     * @param key 键
     */
    public static void remove(String key) {
        Map<String, Object> ctx = contextHolder.get();
        ctx.remove(key);
    }

    /**
     * 清除当前线程的所有上下文数据（必须调用，防止内存泄漏）
     */
    public static void clear() {
        contextHolder.remove();
    }

    /**
     * 获取当前线程上下文中的所有数据（只读快照，用于调试）
     *
     * @return Map 的不可变副本
     */
    public static Map<String, Object> getContextMap() {
        return Map.copyOf(contextHolder.get());
    }
}