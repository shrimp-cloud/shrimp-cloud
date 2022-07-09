package com.wkclz.auth.entity;


import com.wkclz.spring.enums.EnvType;

/**
 * 追踪信息
 */
public class TraceInfo {




    /**
     * 源IP
     */
    private String originIp;

    /**
     * 路由IP
     */
    private String upstreamIp;

    /**
     * 所在服务IP
     */
    private static String serverIp = null;

    /**
     * 环境
     */
    private static EnvType envType = null;


    /**
     * 跟踪ID
     */
    private String traceId;
    /**
     * 跟踪序列号
     */
    private Integer spanId;

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 认证ID
     */
    private Long authId;
    /**
     * 用户ID
     */
    private Long userId;



    public String getOriginIp() {
        return originIp;
    }

    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    public String getUpstreamIp() {
        return upstreamIp;
    }

    public void setUpstreamIp(String upstreamIp) {
        this.upstreamIp = upstreamIp;
    }

    public static String getServerIp() {
        return serverIp;
    }

    public static void setServerIp(String serverIp) {
        TraceInfo.serverIp = serverIp;
    }

    public static EnvType getEnvType() {
        return envType;
    }

    public static void setEnvType(EnvType envType) {
        TraceInfo.envType = envType;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getSpanId() {
        return spanId;
    }

    public void setSpanId(Integer spanId) {
        this.spanId = spanId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
