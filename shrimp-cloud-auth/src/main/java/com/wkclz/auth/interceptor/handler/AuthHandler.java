package com.wkclz.auth.interceptor.handler;

import com.wkclz.auth.config.AuthConfig;
import com.wkclz.auth.helper.AccessHelper;
import com.wkclz.auth.helper.ApiDomainHelper;
import com.wkclz.auth.helper.AuthHelper;
import com.wkclz.common.entity.Result;
import com.wkclz.spring.config.Sys;
import com.wkclz.spring.enums.EnvType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Description: 此处拦截不再加入到 WebMvcConfigurer， 真正的拦截交给网关
 * Created: wangkaicun @ 2017-10-18 下午11:41
 */
@Component
public class AuthHandler {

    @Autowired
    private AuthConfig authConfig;
    @Autowired
    private AuthHelper authHelper;

    public Result preHandle(HttpServletRequest req, HttpServletResponse rep) {

        // 总开关【只允许测试环境关闭】
        if (Sys.CURRENT_ENV != EnvType.PROD && !authConfig.getAuthFilter()) {
            return null;
        }

        // API 安全检测

        if (authConfig.getSecurityDomainApi()) {
            Result apiDomainCheckResult = ApiDomainHelper.checkApiDomains(req, rep);
            if (apiDomainCheckResult != null) {
                return apiDomainCheckResult;
            }
        }

        /**
         * 用户登录检测
         */

        // uri 拦截检测【如果检查通过，无需再检查token】
        boolean checkAccessUriResult = AccessHelper.checkAccessUri(req);
        if (checkAccessUriResult) {
            return null;
        }

        // token 检测
        Result userTokenCheckResult = authHelper.checkUserToken(req, rep);
        if (userTokenCheckResult != null) {
            return userTokenCheckResult;
        }

        // uri 权限检测 TODO
        /*
        boolean userUriAuthCheckResult = true;
        if (!userUriAuthCheckResult){
            return false;
        }
        */
        return null;
    }

}
