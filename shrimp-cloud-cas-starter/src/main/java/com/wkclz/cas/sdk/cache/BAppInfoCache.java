package com.wkclz.cas.sdk.cache;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.SdkConstant;
import com.wkclz.cas.sdk.pojo.appinfo.App;
import com.wkclz.cas.sdk.pojo.appinfo.AppInfo;
import org.apache.commons.collections4.CollectionUtils;
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

    public void refresh(List<String> appCodes) {
        if (appCodes != null) {
            for (String appCode : appCodes) {
                refresh(appCode, SdkConstant.TOTAL_CACKE);
            }
        }
    }

    public void refresh(String appCode) {
        refresh(appCode, SdkConstant.TOTAL_CACKE);
    }

    public void refresh(String appCode, List<String> types) {
        if (StringUtils.isBlank(appCode) || CollectionUtils.isEmpty(types)) {
            return;
        }
        AppInfo appInfo = APP_RESOURCE_CACHE_MAP.get(appCode);
        if (appInfo == null) {
            appInfo = new AppInfo();
        }
        APP_RESOURCE_CACHE_MAP.put(appCode, appInfo);

        if (types.contains(SdkConstant.APP)) {
            App app = appInfoFacade.app(appCode);
            if (app == null) {
                return;
            }
            appInfo.setApp(app);
        }

        if (types.contains(SdkConstant.ROLE)) {
            appInfo.setRoles(appInfoFacade.roles(appCode));
        }
        if (types.contains(SdkConstant.RES)) {
            appInfo.setReses(appInfoFacade.reses(appCode));
        }
        if (types.contains(SdkConstant.API)) {
            appInfo.setApis(appInfoFacade.apis(appCode));
        }
        if (types.contains(SdkConstant.ROLE_RES)) {
            appInfo.setRoleReses(appInfoFacade.roleReses(appCode));
        }
        if (types.contains(SdkConstant.RES_API)) {
            appInfo.setResApis(appInfoFacade.resApis(appCode));
        }
        if (types.contains(SdkConstant.ACCESS_TOKEN)) {
            appInfo.setAccessTokens(appInfoFacade.accessToken(appCode));
        }
    }

}
