package com.wkclz.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * lz 配置
 * wangkc @ 2019-07-21 23:46:08
 */
@Component
@ConfigurationProperties(prefix = "lz")
public class LzConfig {


    /**
     * 默认开启权限拦截
     * 若在能判定用户登录的 tenantId 和 userId 情况下，会自动赋值 Moddel 对象。
     * updaate 和 insert 也可以自动赋值 createUser, lastUpadteUser
     * 后期扩展数据权限拦截
     */
    private Boolean authFilter = true;


    public Boolean getAuthFilter() {
        return authFilter;
    }

    public void setAuthFilter(Boolean authFilter) {
        this.authFilter = authFilter;
    }
}
