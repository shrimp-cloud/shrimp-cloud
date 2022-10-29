package com.wkclz.cas.sdk.facade;

import com.wkclz.cas.sdk.pojo.appinfo.*;

import java.util.List;

public interface AppInfoFacade {
    App app(String appCode);
    List<Role> roles(String appCode);
    List<Res> reses(String appCode);
    List<Api> apis(String appCode);
    List<RoleRes> roleReses(String appCode);
    List<ResApi> resApis(String appCode);


    AppInfo getAppInfo(String appCode);
    List<App> getApps();
    List<String> getUserRoles(String appCode, String userCode);

}
