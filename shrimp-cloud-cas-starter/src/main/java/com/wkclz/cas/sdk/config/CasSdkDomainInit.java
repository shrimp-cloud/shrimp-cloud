package com.wkclz.cas.sdk.config;

import com.wkclz.cas.sdk.cache.BAppInfoCache;
import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.App;
import jakarta.annotation.PostConstruct;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class CasSdkDomainInit {

    private static Logger logger = LoggerFactory.getLogger(CasSdkDomainInit.class);

    @Autowired
    private AppInfoFacade appInfoFacade;
    @Autowired
    private BAppInfoCache appInfoCache;


    @PostConstruct
    public void initDomain() {
        List<App> apps = appInfoFacade.getApps();
        if (CollectionUtils.isNotEmpty(apps)) {
            List<String> appCodes = apps.stream().map(App::getAppCode).toList();
            appInfoCache.refresh(appCodes);
            // ATenantDomainCache.setTenantDomains(apps);
            logger.info("应用信息缓存 {} 条", apps.size());
        }
    }

}
