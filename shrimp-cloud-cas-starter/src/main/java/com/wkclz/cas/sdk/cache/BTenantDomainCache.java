package com.wkclz.cas.sdk.cache;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.App;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Description: 若需要域名转 tenantCode, 需要提前 set 缓存信息。否则只能用header 取租户信息
 * @date : wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class BTenantDomainCache {

    @Autowired
    private AppInfoFacade appInfoFacade;

    private static Map<String, String> TENANT_DOMAINS = null;

    public Map<String, String> get() {
        return TENANT_DOMAINS;
    }

    public synchronized String get(String domain) {
        if (domain == null) {
            return null;
        }

        if (TENANT_DOMAINS == null) {
            List<App> apps = appInfoFacade.getApps();
            TENANT_DOMAINS = new HashedMap();
            try {
                for (App app : apps) {
                    URL url = new URL(app.getDomain());
                    TENANT_DOMAINS.put(url.getHost(), app.getAppCode());
                }
            } catch (Exception e) {
                // do nothing
            }
        }
        return TENANT_DOMAINS.get(domain);
    }

}
