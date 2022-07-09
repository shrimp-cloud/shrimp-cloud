package com.wkclz.spring.constant;

/**
 * 日志链路常量
 */
public interface LogTraceConstant {

    // 日志信息
    public final static String SERVER_IP = "server-ip";
    public final static String ENV_TYPE = "env-type";

    // 跟踪信息
    public final static String TRACE_ID = "trace-id";
    public final static String SPAN_ID = "span-id";
    public final static String UPSTREAM_IP = "upstream-ip";
    public final static String ORIGIN_IP = "origin-ip";

    // 用户信息
    public final static String TENANT_ID = "tenant-id";
    public final static String AUTH_ID = "auth-id";
    public final static String USER_ID = "user-id";


}
