package com.wkclz.cas.sdk.config;

import com.wkclz.cas.sdk.cache.ATenantDomainCache;
import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.pojo.appinfo.App;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;


@Configuration
public class CasSdkDomainInit {

    private static Logger logger = LoggerFactory.getLogger(CasSdkDomainInit.class);

    @Autowired
    private AppInfoFacade appInfoFacade;

    @PostConstruct
    public void initDomain() {
        List<App> apps = appInfoFacade.getApps();
        if (CollectionUtils.isNotEmpty(apps)) {
            ATenantDomainCache.setTenantDomains(apps);
            logger.info("应用域名解析 {} 条", apps.size());
        }
    }

}
