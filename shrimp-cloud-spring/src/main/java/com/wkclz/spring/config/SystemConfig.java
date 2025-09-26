package com.wkclz.spring.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * lz 配置
 * wangkc @ 2019-07-21 23:46:08
 */

@Data
@Configuration
public class SystemConfig {

    @Value("${eureka.client.serviceUrl.defaultZone:null}")
    private String defaultZone;
    @Value("${spring.application.name:APP}")
    private String applicationName;
    @Value("${spring.application.group:shrimp}")
    private String applicationGroup;
    @Value("${spring.profiles.active:dev}")
    private String profiles;


    // 配置解密

    @Value("${shrimp.config.decrypt-aes-key:}")
    private String configDecryptAesKey;


    // 告警邮件发送

    @Value("${alarm.email.enabled:false}")
    private boolean alarmEmailEnabled;
    @Value("${alarm.email.host:smtp.exmail.qq.com}")
    private String alarmEmailHost;
    @Value("${alarm.email.from:alarm@wkclz.com}")
    private String alarmEmailFrom;
    @Value("${alarm.email.password:your_password}")
    private String alarmEmailPassword;
    @Value("${alarm.email.to:admin@wkclz.com}")
    private String alarmEmailTo;


    public boolean isCloud(){
        return !"null".equals(this.defaultZone);
    }
}
