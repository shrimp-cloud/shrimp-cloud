package com.wkclz.cas.sdk.cache;

import java.util.Map;

/**
 * Description: 若需要域名转 tenantCode, 需要提前 set 缓存信息。否则只能用header 取租户信息
 * @date : wangkaicun @ 2019-02-13 20:55:11
 */
public class TenantDomainCache {

    private static Map<String, String> TENANT_DOMAINS = null;

    public static void set(Map<String, String> tenantDomains) {
        TENANT_DOMAINS = tenantDomains;
    }
    public static Map<String, String> get() {
        return TENANT_DOMAINS;
    }
    public static String get(String domain) {
        if (TENANT_DOMAINS == null || domain == null) {
            return null;
        }
        return TENANT_DOMAINS.get(domain);
    }

}
