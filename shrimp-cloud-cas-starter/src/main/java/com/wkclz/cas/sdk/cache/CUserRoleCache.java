package com.wkclz.cas.sdk.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.helper.AuthHelper;
import com.wkclz.common.utils.SecretUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


@Component
public class CUserRoleCache {

    private static final Cache<String, List<String>> USER_ROLES = CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .initialCapacity(10240)
        .maximumSize(1024 * 1024L)
        .expireAfterAccess(30, TimeUnit.MINUTES)
        .build();

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private AppInfoFacade appInfoFacade;

    public synchronized ConcurrentMap<String, List<String>> getAll() {
        return USER_ROLES.asMap();
    }

    public synchronized List<String> get() {
        String token = authHelper.getToken(true);
        String tenantCode = authHelper.getTenantCodeWithDefault();
        String appCode = authHelper.getAppCode();
        String key = SecretUtil.md5(tenantCode + ":" + appCode + ":" + token);
        List<String> roles = USER_ROLES.getIfPresent(key);
        if (roles != null) {
            return roles;
        }
        refresh(key);
        roles = USER_ROLES.getIfPresent(key);
        return roles;
    }

    public void refresh(String key) {
        String userCode = authHelper.getUserCode();
        String tenantCode = authHelper.getTenantCodeWithDefault();
        // String appCode = authHelper.getAppCode();
        // 应用未分微服务，需要查询所有应用下的角色
        List<String> userRoles = appInfoFacade.getUserRoles(tenantCode, userCode);
        if (CollectionUtils.isEmpty(userRoles)) {
            USER_ROLES.put(key, new ArrayList<>());
            return;
        }
        USER_ROLES.put(key, userRoles);
    }
}
