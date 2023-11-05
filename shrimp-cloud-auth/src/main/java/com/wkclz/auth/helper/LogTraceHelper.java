package com.wkclz.auth.helper;

import com.wkclz.auth.entity.TraceInfo;
import com.wkclz.auth.entity.User;
import com.wkclz.common.utils.SecretUtil;
import com.wkclz.spring.config.Sys;
import com.wkclz.spring.constant.LogTraceConstant;
import com.wkclz.spring.enums.EnvType;
import com.wkclz.spring.helper.IpHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 仅在应用入口处可用此功能
 */
@Component
public class LogTraceHelper {

    @Autowired
    private AuthHelper authHelper;

    public TraceInfo checkTraceInfo(HttpServletRequest req, HttpServletResponse rep){

        // 如果已经存在，不再请求第二次，直接返回结果
        String traceId = MDC.get(LogTraceConstant.TRACE_ID);

        if (StringUtils.isBlank(traceId)){
            traceId = req.getHeader(LogTraceConstant.TRACE_ID);
            if (StringUtils.isBlank(traceId)) {
                traceId = SecretUtil.getJavaUuid();
            }
        } else {
            // 说明已经处理过了，不需要再生成相关信息
            TraceInfo traceInfo = new TraceInfo();
            traceInfo.setOriginIp(MDC.get(LogTraceConstant.ORIGIN_IP));
            traceInfo.setUpstreamIp(MDC.get(LogTraceConstant.UPSTREAM_IP));
            traceInfo.setTraceId(MDC.get(LogTraceConstant.TRACE_ID));
            String spanId = MDC.get(LogTraceConstant.SPAN_ID);
            if (StringUtils.isNotBlank(spanId)){
                Integer integer = Integer.valueOf(spanId);
                traceInfo.setSpanId(integer);
            }

            String tenantId = MDC.get(LogTraceConstant.TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)){
                Long l = Long.valueOf(tenantId);
                traceInfo.setTenantId(l);
            }

            String authId = MDC.get(LogTraceConstant.AUTH_ID);
            if (StringUtils.isNotBlank(authId)){
                Long l = Long.valueOf(authId);
                traceInfo.setAuthId(l);
            }

            String userId = MDC.get(LogTraceConstant.USER_ID);
            if (StringUtils.isNotBlank(userId)){
                Long l = Long.valueOf(userId);
                traceInfo.setUserId(l);
            }
            return traceInfo;
        }

        TraceInfo traceInfo = new TraceInfo();
        traceInfo.setAuthId(-1L);
        traceInfo.setUserId(-1L);

        Long tenantId = authHelper.getTenantId();
        User user = authHelper.checkUserSession(req);
        traceInfo.setTenantId(tenantId);
        if (user != null){
            traceInfo.setAuthId(user.getAuthId());
            traceInfo.setUserId(user.getUserId());
        }

        // serverIp
        String serverIp = MDC.get(LogTraceConstant.SERVER_IP);
        if (serverIp == null){ serverIp = req.getHeader(LogTraceConstant.SERVER_IP); }
        if (serverIp == null) { serverIp = IpHelper.getServerIp(); }
        if (TraceInfo.getServerIp() == null || !serverIp.equals(TraceInfo.getServerIp())){
            TraceInfo.setServerIp(serverIp);
        }

        // envType
        String envType = MDC.get(LogTraceConstant.ENV_TYPE);
        if (envType == null){ envType = req.getHeader(LogTraceConstant.ENV_TYPE); }
        if (envType == null) { envType = Sys.CURRENT_ENV.name(); }
        if (TraceInfo.getEnvType() == null || !envType.equals(TraceInfo.getEnvType().name())){
            TraceInfo.setEnvType(EnvType.valueOf(envType));
        }

        // 为储藏室，直接生成
        traceInfo.setTraceId(traceId);

        // spanId
        String spanId = MDC.get(LogTraceConstant.SPAN_ID);
        if (spanId == null) { spanId = req.getHeader(LogTraceConstant.SPAN_ID); }
        if (spanId == null) { spanId = "0"; }
        Integer newSpanId = Integer.parseInt(spanId) + 1;
        traceInfo.setSpanId(newSpanId);

        traceInfo.setOriginIp(IpHelper.getOriginIp(req));
        traceInfo.setUpstreamIp(IpHelper.getUpstreamIp(req));

        /* 检查 cookie, header 【太多内容了有风险】
        Enumeration<String> headerNames = req.getHeaderNames();
        if (headerNames != null){
            while (headerNames.hasMoreElements()){
                String s = headerNames.nextElement();
                MDC.put(s, req.getHeader(s));
            }
        }
        Cookie[] cookies = req.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                MDC.put(cookie.getName(), cookie.getValue());
            }
        }
        */

        // 日志信息
        MDC.put(LogTraceConstant.SERVER_IP, TraceInfo.getServerIp());
        MDC.put(LogTraceConstant.ENV_TYPE, TraceInfo.getEnvType().name());

        // 跟踪信息
        MDC.put(LogTraceConstant.TRACE_ID, traceId);
        MDC.put(LogTraceConstant.SPAN_ID, spanId);
        MDC.put(LogTraceConstant.UPSTREAM_IP, traceInfo.getUpstreamIp());
        MDC.put(LogTraceConstant.ORIGIN_IP, traceInfo.getOriginIp());

        // 用户信息
        MDC.put(LogTraceConstant.TENANT_ID, traceInfo.getTenantId() + "");
        MDC.put(LogTraceConstant.AUTH_ID, traceInfo.getAuthId() + "");
        MDC.put(LogTraceConstant.USER_ID, traceInfo.getUserId() + "");

        return traceInfo;
    }

}
