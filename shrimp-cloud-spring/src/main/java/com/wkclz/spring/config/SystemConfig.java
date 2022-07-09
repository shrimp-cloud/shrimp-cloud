package com.wkclz.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * lz 配置
 * wangkc @ 2019-07-21 23:46:08
 */

@Configuration
public class SystemConfig {

    @Value("${eureka.client.serviceUrl.defaultZone:null}")
    private String defaultZone;
    @Value("${spring.application.name:APP}")
    private String applicationName;
    @Value("${spring.application.group:CMS}")
    private String applicationGroup;
    @Value("${spring.profiles.active:dev}")
    private String profiles;


    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationGroup() {
        return applicationGroup;
    }

    public void setApplicationGroup(String applicationGroup) {
        this.applicationGroup = applicationGroup;
    }

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public boolean isCloud(){
        return !"null".equals(this.defaultZone);
    }
}
