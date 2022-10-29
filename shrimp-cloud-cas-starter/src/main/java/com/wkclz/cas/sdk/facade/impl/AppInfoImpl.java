package com.wkclz.cas.sdk.facade.impl;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnMissingBean
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
    public List<App> getApps() {
        return new ArrayList<>();
    }
    @Override
    public List<String> getUserRoles(String appCode, String userCode) {
        return new ArrayList<>();
    }


}
