package com.wkclz.cas.sdk.helper;

import com.wkclz.cas.sdk.config.CasSdkConfig;
import com.wkclz.cas.sdk.pojo.SdkConstant;
import com.wkclz.cas.sdk.pojo.UserInfo;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.exception.BizException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthHelper {

    @Autowired
    private CasSdkConfig casSdkConfig;

    public String getUsercode() {
        return getClaimValue(SdkConstant.USER_INFO_USER_CODE);
    }
    public String getUsername() {
        return getClaimValue(SdkConstant.USER_INFO_USER_NAME);
    }
    public String getNickname() {
        return getClaimValue(SdkConstant.USER_INFO_NICK_NAME);
    }
    public String getAvatar() {
        return getClaimValue(SdkConstant.USER_INFO_USER_AVATAR);
    }

    public UserInfo getUserInfoIfLogin() {
        String token = geToken();
        if (token == null) {
            return null;
        }
        return getUserInfo();
    }

    public UserInfo getUserInfo() {
        String token = geToken();
        if (token == null) {
            throw BizException.error(ResultStatus.TOKEN_UNLL);
        }
        Claims claims = getClaims(token);
        UserInfo userInfo = new UserInfo();
        userInfo.setUsercode(getClaimValue(claims, SdkConstant.USER_INFO_USER_CODE));
        userInfo.setUsername(getClaimValue(claims, SdkConstant.USER_INFO_USER_NAME));
        userInfo.setNickname(getClaimValue(claims, SdkConstant.USER_INFO_NICK_NAME));
        userInfo.setAvatar(getClaimValue(claims, SdkConstant.USER_INFO_USER_AVATAR));
        return userInfo;
    }

    private String geToken() {
        HttpServletRequest request = getRequest();
        return request.getHeader(SdkConstant.TOKEN_NAME);
    }
    private static Claims parseToken(String secret, String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
    private Claims getClaims(String token) {
        String tokenSecret = casSdkConfig.getTokenSecret();
        return parseToken(tokenSecret, token);
    }
    private String getClaimValue(String claimKey) {
        String token = geToken();
        Claims claims = getClaims(token);
        Object o = claims.get(claimKey);
        return o == null ? null:o.toString();
    }
    private String getClaimValue(Claims claims, String claimKey) {
        Object o = claims.get(claimKey);
        return o == null ? null:o.toString();
    }

    /**
     * 获取当前请求
     * @return
     */
    private static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null){
            return null;
        }
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

}
