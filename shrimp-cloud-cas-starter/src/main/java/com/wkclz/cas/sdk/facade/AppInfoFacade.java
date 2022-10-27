package com.wkclz.cas.sdk.facade;

import com.wkclz.cas.sdk.pojo.appinfo.App;

import java.util.List;

public interface AppInfoFacade {

    List<App> getApps();

    List<String> getUserRoles(String userCode);

}
