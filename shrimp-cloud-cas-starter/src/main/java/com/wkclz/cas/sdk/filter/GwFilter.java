package com.wkclz.cas.sdk.filter;

import cn.hutool.core.util.StrUtil;
import com.wkclz.cas.sdk.cache.BAppInfoCache;
import com.wkclz.cas.sdk.cache.DUserApiCache;
import com.wkclz.cas.sdk.helper.AccessTokenHelper;
import com.wkclz.cas.sdk.helper.AuthHelper;
import com.wkclz.cas.sdk.helper.ResponseHelper;
import com.wkclz.cas.sdk.pojo.SdkConstant;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.entity.Result;
import com.wkclz.common.exception.BizException;
import com.wkclz.common.utils.SecretUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class GwFilter extends OncePerRequestFilter {

    private final static String GW_FILTER_LOG_KEY = "GW_FILTER_LOG";
    private final static AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private static Logger logger = LoggerFactory.getLogger(GwFilter.class);

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private BAppInfoCache appInfoCache;
    @Autowired
    private DUserApiCache dUserApiCache;
    @Autowired
    private AccessTokenHelper accessTokenHelper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        MDC.remove(SdkConstant.HEADER_APP_CODE);
        MDC.remove(SdkConstant.HEADER_TENANT_CODE);
        MDC.remove("user");
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String ua = request.getHeader("User-Agent");

        // druid 自身带密码，跳过验证
        boolean druid = ANT_PATH_MATCHER.match("/druid/**", uri);
        if (druid) {
            MDC.put(GW_FILTER_LOG_KEY, "druid");
            chain.doFilter(request, response);
            return;
        }

        // TODO 请求日志
        boolean match = ANT_PATH_MATCHER.match("/public/**", uri);
        if (match) {
            MDC.put(GW_FILTER_LOG_KEY, "public");
            chain.doFilter(request, response);
            return;
        }

        // sign 签名验证
        String appId = request.getHeader("app-id");
        String sign = request.getHeader("sign");
        if (StringUtils.isNotBlank(appId) && StringUtils.isNotBlank(sign)) {
            boolean b = accessTokenHelper.deSign(appId, sign);
            if (b) {
                MDC.put(GW_FILTER_LOG_KEY, appId);
                chain.doFilter(request, response);
                return;
            }
            String msg = StrUtil.format("{}|{}|{}|{}|sign faild", appId, method, uri, ua);
            logger.error(msg);
            ResponseHelper.responseError(response, Result.error(msg));
            return;
        }

        String token = authHelper.getToken();
        if (token == null) {
            Result msg = Result.error(ResultStatus.TOKEN_UNLL);
            logger.error("{}|{}|{}|no token", method, uri, ua);
            ResponseHelper.responseError(response, msg);
            return;
        }

        String tokenKey = SecretUtil.md5(token);
        BoundValueOperations<String, String> ops = stringRedisTemplate.boundValueOps(tokenKey);
        token = ops.get();
        if (token == null) {
            Result msg = Result.error(ResultStatus.LOGIN_TIMEOUT);
            logger.error("{}|{}|{}|expire token", method, uri, ua);
            ResponseHelper.responseError(response, msg);
            return;
        }
        ops.expire(30L, TimeUnit.MINUTES);

        String userCode;
        try {
            userCode = authHelper.getUserCode();

            /* 因为所有应用都走同一个入口，无法获取模块应用编码。无法使用区分授权方式
            AppInfo appInfo = appInfoCache.get(authHelper.getAppCode());
            String authType = appInfo.getApp().getAuthType();
            if ("TOKEN".equals(authType)) {
                chain.doFilter(request, response);
                return;
            }
            if ("TOKEN_API".equals(authType) || "TOKEN_API_BUTTON".equals(authType) ) {
                String method = request.getMethod();
                boolean b = dUserApiCache.get(method, uri);
                if (!b) {
                    Result msg = Result.error("没有接口权限: " + method + ":" + uri);
                    msg.setCode(HttpStatus.FORBIDDEN.value());
                    ResponseHelper.responseError(response, msg);
                    return;
                }
                chain.doFilter(request, response);
                return;
            }
            */


            // 全部都启用接口级鉴权 begin
            boolean b = dUserApiCache.get(method, uri);
            if (!b) {
                Result msg = Result.error("没有接口权限: " + method + ":" + uri);
                logger.error("{}|{}|{}|{}|no permission", userCode, method, uri, ua);
                msg.setCode(HttpStatus.FORBIDDEN.value());
                ResponseHelper.responseError(response, msg);
                return;
            }
            // 全部都启用接口级鉴权 end
        } catch (Exception e) {
            String msg = e.getMessage();
            Result error = Result.error(msg);
            if (e instanceof BizException) {
                BizException be = (BizException)e;
                error.setCode(be.getCode());
            }
            logger.error("{}|{}|{}|err token", method, uri, ua);
            ResponseHelper.responseError(response, error);
            return;
        }

        MDC.put(GW_FILTER_LOG_KEY, userCode);
        chain.doFilter(request, response);

        /*
        Result msg = Result.error("应用 " + appCode + " 鉴权配置异常:" + uri);
        msg.setCode(HttpStatus.FORBIDDEN.value());
        ResponseHelper.responseError(response, msg);
        */
    }

}

