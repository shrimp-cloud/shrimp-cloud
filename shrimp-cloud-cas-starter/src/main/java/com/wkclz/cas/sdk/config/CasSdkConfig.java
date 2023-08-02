package com.wkclz.cas.sdk.config;

import com.wkclz.cas.sdk.facade.AppInfoFacade;
import com.wkclz.cas.sdk.facade.impl.AppInfoImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CasSdkConfig {

    /**
     * TODO 线上请覆盖此值，并定制更换。
     * 更新时会因为 token 无法解密而下线，需要重新登录，可考虑扩展为动态密钥，有一定有效期
     */
    @Value(("${cas.sdk.app-code:default}"))
    private String appCode;
    @Value(("${cas.sdk.user-default-roles:default}"))
    private String userDefaultRoles;
    @Value(("${cas.sdk.token.secret:shrimp.chlinhchentclinxmeicyxiuhchua.shrimp.chlinhchentclinxmeicyxiuhchua.shrimp}"))
    private String tokenSecret;


    /**
     * 用于请求服务端的配置
     */
    @Value(("${cas.sdk.app-id:default}"))
    private String appId;
    @Value(("${cas.sdk.app-secret:default}"))
    private String appSecret;


    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getUserDefaultRoles() {
        return userDefaultRoles;
    }

    public void setUserDefaultRoles(String userDefaultRoles) {
        this.userDefaultRoles = userDefaultRoles;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    @Bean
    @ConditionalOnMissingBean
    public AppInfoFacade getAppInfoFacade() {
        return new AppInfoImpl();
    }

}
