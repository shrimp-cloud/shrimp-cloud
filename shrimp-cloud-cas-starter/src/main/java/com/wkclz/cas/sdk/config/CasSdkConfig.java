package com.wkclz.cas.sdk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CasSdkConfig {

    /**
     * TODO 线上请覆盖此值，并定制更换。
     * 更新时会因为 token 无法解密而下线，需要重新登录，可考虑扩展为动态密钥，有一定有效期
     */
    @Value(("${cas.sso.app-code:default}"))
    private String appCode;
    @Value(("${cas.sso.token.secret:shrimp.chlinhchentclinxmeicyxiuhchua.shrimp.chlinhchentclinxmeicyxiuhchua.shrimp}"))
    private String tokenSecret;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }
}