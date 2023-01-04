package com.wkclz.cas.sdk.cache;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.App;
import com.wkclz.cas.sdk.pojo.appinfo.AppInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BAppInfoCache {

    private static final Map<String, AppInfo> APP_RESOURCE_CACHE_MAP = new ConcurrentHashMap<>();

    @Autowired
    private AppInfoFacade appInfoFacade;

    public synchronized List<AppInfo> get() {
        return APP_RESOURCE_CACHE_MAP.values().stream().toList();
    }

    public synchronized AppInfo get(String appCode) {
        if (StringUtils.isBlank(appCode)) {
            return null;
        }
        AppInfo appInfo = APP_RESOURCE_CACHE_MAP.get(appCode);
        if (appInfo != null) {
            return appInfo.getApp() == null ? null : appInfo;
        }
        // 数据不存在，重新加载
        refresh(appCode);
        appInfo = APP_RESOURCE_CACHE_MAP.get(appCode);
        return (appInfo == null || appInfo.getApp() == null) ? null : appInfo;
    }

    public void refresh(String appCode) {
        refresh(appCode, "APP,ROLE,RES,API,ROLE_RES,RES_API");
    }

    public void refresh(String appCode, String type) {
        if (StringUtils.isBlank(appCode) || StringUtils.isBlank(type)) {
            return;
        }
        AppInfo appInfo = APP_RESOURCE_CACHE_MAP.get(appCode);
        if (appInfo == null) {
            appInfo = new AppInfo();
        }
        APP_RESOURCE_CACHE_MAP.put(appCode, appInfo);

        List<String> types = Arrays.asList(type.split(","));
        if (types.contains("APP")) {
            App app = appInfoFacade.app(appCode);
            if (app == null) {
                return;
            }
            appInfo.setApp(app);
        }

        if (types.contains("ROLE")) {
            appInfo.setRoles(appInfoFacade.roles(appCode));
        }
        if (types.contains("RES")) {
            appInfo.setReses(appInfoFacade.reses(appCode));
        }
        if (types.contains("API")) {
            appInfo.setApis(appInfoFacade.apis(appCode));
        }
        if (types.contains("ROLE_RES")) {
            appInfo.setRoleReses(appInfoFacade.roleReses(appCode));
        }
        if (types.contains("RES_API")) {
            appInfo.setResApis(appInfoFacade.resApis(appCode));
        }
    }

}
