package com.wkclz.auth.interceptor;


import com.alibaba.fastjson2.JSON;
import com.wkclz.auth.entity.AccessLog;
import com.wkclz.auth.helper.AccessHelper;
import com.wkclz.auth.helper.LogTraceHelper;
import com.wkclz.auth.interceptor.handler.AuthHandler;
import com.wkclz.common.emuns.ResultStatus;
import com.wkclz.common.entity.Result;
import com.wkclz.spring.config.SystemConfig;
import com.wkclz.spring.constant.Queue;
import com.wkclz.spring.helper.ResponseHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@WebFilter(filterName = "gwFilter", urlPatterns = "/*")
public class GwFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(GwFilter.class);

    @Autowired
    private AuthHandler authHandler;
    @Autowired
    private SystemConfig systemConfig;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LogTraceHelper logTraceHelper;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        MDC.clear();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        try {
            logTraceHelper.checkTraceInfo(httpRequest, httpResponse);
        } catch (Exception e) {
            Result error = Result.error(e.getMessage());
            ResponseHelper.responseError(httpResponse, error);
            return;
        }


        // 不走微服务才需要此过程
        if (systemConfig.isCloud()) {
            chain.doFilter(request, response);
            return;
        }


        // 非微服务 【替代网关上的逻辑】


        // 记录日志
        AccessLog accessLog = AccessHelper.getAccessLog(httpRequest);
        if (accessLog != null) {
            String key = Queue.LOGGER_QUEUE_PREFIX + ":" + systemConfig.getProfiles() + ":" + systemConfig.getApplicationGroup();
            String jsonString = JSON.toJSONString(accessLog);
            //消息入队列
            redisTemplate.opsForList().leftPush(key, jsonString);
        }

        Result result = authHandler.preHandle(httpRequest, httpResponse);
        String uri = httpRequest.getRequestURI();
        String ua = httpRequest.getHeader("User-Agent");
        logger.info("request {}, auth: {}, UA: {}", uri, (result == null), ua);

        if (result != null) {
            try {
                InputStream in = request.getInputStream();
                String body = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
                if (StringUtils.isNotBlank(body)) {
                    logger.warn("Request interception, request body: {}", body);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            ResponseHelper.responseError(httpResponse, result);
            return;
        }

        if (uri.startsWith("/swagger-") || uri.startsWith("/v3/api-docs") || uri.startsWith("/doc.html") || uri.startsWith("/webjars/")) {
            chain.doFilter(request, response);
            return;
        }

        String[] uriSplit = uri.split("/");
        if (uriSplit.length < 3) {
            ResponseHelper.responseError(httpResponse, Result.error(ResultStatus.ERROR_ROUTER));
            return;
        }
        String module = uriSplit[1];
        uri = uri.substring(1 + module.length());

        // 标识来源模块
        httpRequest.setAttribute("module", module);
        httpRequest.getRequestDispatcher(uri).forward(request, response);

        // 永远不应该走到这里
        // chain.doFilter(request,response);
    }


}

