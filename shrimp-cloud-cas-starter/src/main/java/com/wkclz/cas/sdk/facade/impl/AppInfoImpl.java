package com.wkclz.cas.sdk.facade.impl;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.*;

import java.util.ArrayList;
import java.util.List;

public class AppInfoImpl implements AppInfoFacade {

    @Override
    public App app(String appCode) {
        return null;
    }
    @Override
    public List<Role> roles(String appCode) {
        return null;
    }
    @Override
    public List<Res> reses(String appCode) {
        return null;
    }
    @Override
    public List<Api> apis(String appCode) {
        return null;
    }
    @Override
    public List<RoleRes> roleReses(String appCode) {
        return null;
    }
    @Override
    public List<ResApi> resApis(String appCode) {
        return null;
    }
    @Override
    public List<AccessToken> accessTokens(String appCode) {
        return null;
    }

    @Override
    public List<App> getApps() {
        return new ArrayList<>();
    }
    @Override
    public List<String> getUserRoles(String tenantCode, String userCode) {
        return new ArrayList<>();
    }
    @Override
    public List<String> getUserRoles(String tenantCode, String userCode, String appCode) {
        return new ArrayList<>();
    }

    @Override
    public List<CacheRecord> getCacheRecords() {
        return null;
    }

}
