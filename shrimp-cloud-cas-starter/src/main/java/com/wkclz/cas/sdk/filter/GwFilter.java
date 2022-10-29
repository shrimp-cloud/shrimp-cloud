package com.wkclz.cas.sdk.filter;

import com.wkclz.cas.sdk.cache.AppInfoCache;
import com.wkclz.cas.sdk.cache.DUserApiCache;
import com.wkclz.cas.sdk.helper.AuthHelper;
import com.wkclz.cas.sdk.helper.ResponseHelper;
import com.wkclz.cas.sdk.pojo.appinfo.AppInfo;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class GwFilter extends OncePerRequestFilter {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private static Logger logger = LoggerFactory.getLogger(GwFilter.class);

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private DUserApiCache dUserApiCache;
    @Autowired
    private AppInfoCache appInfoCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        MDC.clear();
        String uri = request.getRequestURI();
        String ua = request.getHeader("User-Agent");

        // TODO 请求日志

        boolean match = ANT_PATH_MATCHER.match("/public/**", uri);
        if (match) {
            logger.info("request: {}, write list, UA: {}", uri, ua);
            chain.doFilter(request, response);
            return;
        }

        try {
            String userCode = authHelper.getUserCode();
            logger.info("request: {}, userCode: {}, UA: {}", uri, userCode, ua);
        } catch (Exception e) {
            String msg = e.getMessage();
            Result error = Result.error(msg);
            if (e instanceof BizException) {
                BizException be = (BizException)e;
                error.setCode(be.getCode());
            }
            logger.info("request: {}, auth faild: {}, UA: {}", uri, msg, ua);
            ResponseHelper.responseError(response, error);
            return;
        }
        String appCode = authHelper.getAppCode();

        AppInfo appInfo = appInfoCache.get(appCode);
        String authType = appInfo.getApp().getAuthType();

        if ("TOKEN".equals(authType)) {
            chain.doFilter(request, response);
            return;
        }
        if ("TOKEN_API".equals(authType) || "TOKEN_API_BUTTON".equals(authType) ) {
            String method = request.getMethod();
            boolean b = dUserApiCache.get(method, uri);
            if (!b) {
                Result msg = Result.error("没有接口权限: " + uri);
                msg.setCode(HttpStatus.FORBIDDEN.value());
                ResponseHelper.responseError(response, msg);
                return;
            }
        }

        Result msg = Result.error("应用 " + appCode + " 鉴权配置异常:" + uri);
        msg.setCode(HttpStatus.FORBIDDEN.value());
        ResponseHelper.responseError(response, msg);
    }

}

