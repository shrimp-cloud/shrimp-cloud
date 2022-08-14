package com.wkclz.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * lz 配置
 * wangkc @ 2019-07-21 23:46:08
 */
@Component
@Configuration
public class AuthConfig {


    /**
     * 默认开启权限拦截
     * 若在能判定用户登录的 tenantId 和 userId 情况下，会自动赋值 Moddel 对象。
     * updaate 和 insert 也可以自动赋值 createUser, lastUpadteUser
     * 后期扩展数据权限拦截
     */
    @Value("${shrimp.cloud.auth.auth-filter:true}")
    private Boolean authFilter;
    @Value("${shrimp.cloud.auth.security.domain.api:true}")
    private Boolean securityDomainApi;
    @Value("${shrimp.cloud.auth.security.domain.tenant:true}")
    private Boolean securityDomainTenant;


    public Boolean getAuthFilter() {
        return authFilter;
    }

    public void setAuthFilter(Boolean authFilter) {
        this.authFilter = authFilter;
    }


    public Boolean getSecurityDomainApi() {
        return securityDomainApi;
    }

    public void setSecurityDomainApi(Boolean securityDomainApi) {
        this.securityDomainApi = securityDomainApi;
    }

    public Boolean getSecurityDomainTenant() {
        return securityDomainTenant;
    }

    public void setSecurityDomainTenant(Boolean securityDomainTenant) {
        this.securityDomainTenant = securityDomainTenant;
    }
}
