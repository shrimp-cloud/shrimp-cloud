package com.wkclz.cas.sdk.helper;

import com.wkclz.cas.sdk.config.CasSdkConfig;
import com.wkclz.cas.sdk.pojo.SdkConstant;
import com.wkclz.cas.sdk.pojo.UserInfo;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.exception.BizException;
import com.wkclz.spring.helper.RequestHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthHelper {

    @Autowired
    private CasSdkConfig casSdkConfig;

    public String getToken() {
        return geTokenFromRequest();
    }
    public String getToken(boolean force) {
        String token = geTokenFromRequest();
        if (force && token == null) {
            throw BizException.error(ResultStatus.TOKEN_UNLL);
        }
        return token;
    }
    public String getAppCode() {
        return getAppCodeFromRequest();
    }
    public String getUserCode() {
        return getClaimValue(SdkConstant.USER_INFO_USER_CODE);
    }
    public String getUsername() {
        return getClaimValue(SdkConstant.USER_INFO_USER_NAME);
    }
    public String getTenantCode() {
        String tenantCode = getTenantCodeFromRequest();
        if (tenantCode == null) {
            throw BizException.error(ResultStatus.TENANT_NULL);
        }
        return tenantCode;
    }
    public String getTenantCodeWithDefault() {
        String tenantCode = getTenantCodeFromRequest();
        if (tenantCode == null) {
            tenantCode = "default";
        }
        return tenantCode;
    }
    public String getTenantCode(boolean nullable) {
        String tenantCode = getTenantCodeFromRequest();
        if (!nullable && tenantCode == null) {
            throw BizException.error(ResultStatus.TENANT_NULL);
        }
        return tenantCode;
    }
    public String getOpenId() {
        return getClaimValue(SdkConstant.USER_INFO_OPEN_ID);
    }

    public UserInfo getUserInfoIfLogin() {
        String token = geTokenFromRequest();
        if (token == null) {
            return null;
        }
        return getUserInfo();
    }

    public UserInfo getUserInfo() {
        String token = geTokenFromRequest();
        if (token == null) {
            throw BizException.error(ResultStatus.TOKEN_UNLL);
        }
        Claims claims = getClaims(token);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserCode(getClaimValue(claims, SdkConstant.USER_INFO_USER_CODE));
        userInfo.setUsername(getClaimValue(claims, SdkConstant.USER_INFO_USER_NAME));
        return userInfo;
    }

    private String getAppCodeFromRequest() {

        String appCode = MDC.get(SdkConstant.HEADER_APP_CODE);
        if (appCode != null){
            return appCode;
        }

        HttpServletRequest request = RequestHelper.getRequest();
        if (request == null) {
            throw BizException.error("request is not from the web");
        }
        appCode = request.getHeader(SdkConstant.HEADER_APP_CODE);
        if (appCode != null) {
            MDC.put(SdkConstant.HEADER_APP_CODE, appCode);
            return appCode;
        }
        throw BizException.error(ResultStatus.APP_CODE_NULL);
        /*
        String domain = RequestHelper.getFrontDomain(request);
        if (StringUtils.isBlank(domain)) {
            throw BizException.error("can not get domain from the request: {}", RequestHelper.getRequestUrl());
        }

        tenantCode = TenantDomainCache.get(domain);
        if (tenantCode != null) {
            MDC.put(SdkConstant.TENANT_CODE, tenantCode);
            return tenantCode;
        }
        throw BizException.error("can not get tenant info, please set tenant-code in header or set tenant-domain-cache");
        */
    }

    private String getTenantCodeFromRequest() {

        String tenantCode = MDC.get(SdkConstant.HEADER_TENANT_CODE);
        if (tenantCode != null){
            return tenantCode;
        }

        HttpServletRequest request = RequestHelper.getRequest();
        if (request == null) {
            throw BizException.error("request is not from the web");
        }
        tenantCode = request.getHeader(SdkConstant.HEADER_TENANT_CODE);
        if (tenantCode != null) {
            MDC.put(SdkConstant.HEADER_TENANT_CODE, tenantCode);
            return tenantCode;
        }

        /*
        String domain = RequestHelper.getFrontDomain(request);
        if (StringUtils.isBlank(domain)) {
            throw BizException.error("can not get domain from the request: {}", RequestHelper.getRequestUrl());
        }
        tenantCode = ATenantDomainCache.get(domain);
        if (tenantCode != null) {
            MDC.put(SdkConstant.HEADER_TENANT_CODE, tenantCode);
        }
        */

        return null;
    }

    private static String geTokenFromRequest() {
        HttpServletRequest request = RequestHelper.getRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader(SdkConstant.HEADER_TOKEN_NAME);
    }

    private static Claims parseToken(String secret, String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Claims getClaims(String token) {
        String tokenSecret = casSdkConfig.getTokenSecret();
        return parseToken(tokenSecret, token);
    }

    private String getClaimValue(String claimKey) {
        String token = geTokenFromRequest();
        if (token == null) {
            throw BizException.error(ResultStatus.TOKEN_UNLL);
        }
        Claims claims = getClaims(token);
        Object o = claims.get(claimKey);
        return o == null ? null:o.toString();
    }

    private String getClaimValue(Claims claims, String claimKey) {
        Object o = claims.get(claimKey);
        return o == null ? null:o.toString();
    }

}
