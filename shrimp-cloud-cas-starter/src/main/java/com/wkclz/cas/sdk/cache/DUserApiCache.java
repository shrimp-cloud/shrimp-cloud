package com.wkclz.cas.sdk.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wkclz.cas.sdk.helper.AuthHelper;
import com.wkclz.cas.sdk.pojo.appinfo.Api;
import com.wkclz.cas.sdk.pojo.appinfo.AppInfo;
import com.wkclz.cas.sdk.pojo.appinfo.ResApi;
import com.wkclz.cas.sdk.pojo.appinfo.RoleRes;
import com.wkclz.common.exception.BizException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DUserApiCache {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private static final Cache<String, Boolean> USER_APIS = CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .initialCapacity(10240)
        .maximumSize(1024 * 1024L)
        .expireAfterAccess(30, TimeUnit.MINUTES)
        .build();

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private BAppInfoCache appInfoCache;
    @Autowired
    private CUserRoleCache cUserRoleCache;

    public synchronized boolean get(String mathod, String uri) {
        String appCode = "cas"; //authHelper.getAppCode();
        String userCode = authHelper.getUserCode();
        String key = appCode + ":" + userCode + ":" + mathod + ":" + uri;
        Boolean access = USER_APIS.getIfPresent(key);
        if (access != null) {
            return access;
        }
        refresh(appCode, userCode, mathod, uri);
        access = USER_APIS.getIfPresent(key);
        return access != null && access;
    }

    public void refresh(String appCode, String userCode, String mathod, String uri) {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(userCode) || StringUtils.isBlank(uri) || StringUtils.isBlank(mathod)) {
            throw BizException.error("应用信息不全，无法鉴权！");
        }

        String key = appCode + ":" + userCode + ":" + mathod + ":" + uri;

        List<String> userRoles = cUserRoleCache.get();
        if (CollectionUtils.isEmpty(userRoles)) {
            USER_APIS.put(key, false);
            return;
        }
        List<AppInfo> appinfos = appInfoCache.get();
        if (CollectionUtils.isEmpty(appinfos)) {
            USER_APIS.put(key, false);
            return;
        }

        // 获取所有 roleRes
        List<RoleRes> roleReses = new ArrayList<>();
        appinfos.forEach(t -> roleReses.addAll(t.getRoleReses()));
        List<String> resCodes = roleReses.stream().filter(t -> userRoles.contains(t.getRoleCode())).map(RoleRes::getResCode).toList();
        if (CollectionUtils.isEmpty(resCodes)) {
            USER_APIS.put(key, false);
            return;
        }


        // 获取所有 apiCodes
        List<ResApi> resApis = new ArrayList<>();
        appinfos.forEach(t -> resApis.addAll(t.getResApis()));
        List<String> apiCodes = resApis.stream().filter(t -> resCodes.contains(t.getResCode())).map(ResApi::getApiCode).toList();
        if (CollectionUtils.isEmpty(apiCodes)) {
            USER_APIS.put(key, false);
            return;
        }

        // 获取所有 apis
        for (AppInfo appinfo : appinfos) {
            List<Api> apis = appinfo.getApis();
            for (Api api : apis) {
                if (ANT_PATH_MATCHER.match(api.getApiUri(), uri) && mathod.equals(api.getApiMathod())) {
                    USER_APIS.put(key, true);
                    return;
                }
            }
        }

        // 无权限
        USER_APIS.put(key, false);
    }

    public void invalidateAll() {
        USER_APIS.invalidateAll();
        USER_APIS.cleanUp();
    }

}
