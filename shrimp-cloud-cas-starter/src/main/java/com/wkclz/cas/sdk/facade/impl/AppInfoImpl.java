package com.wkclz.cas.sdk.facade.impl;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.App;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnMissingBean
public class AppInfoImpl implements AppInfoFacade {

    public List<App> getApps() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getUserRoles(String userCode) {
        return new ArrayList<>();
    }

}
