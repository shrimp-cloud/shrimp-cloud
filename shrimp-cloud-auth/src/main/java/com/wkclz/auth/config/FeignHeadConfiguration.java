package com.wkclz.auth.config;


import com.wkclz.spring.constant.LogTraceConstant;
import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


/**
 * 自定义的请求头处理类，处理服务发送时的请求头；
 * 将服务接收到的请求头中的uniqueId和token字段取出来，并设置到新的请求头里面去转发给下游服务
 * 比如A服务收到一个请求，请求头里面包含uniqueId和token字段，A处理时会使用Feign客户端调用B服务
 * 那么uniqueId和token这两个字段就会添加到请求头中一并发给B服务；
 *
 * @author wangkc4
 * @create 2019/09/15 14:59:25
 * @since 1.0.0
 */
@Configuration
public class FeignHeadConfiguration {
    private final static Logger logger = LoggerFactory.getLogger(FeignHeadConfiguration.class);

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Map<String, String> map = MDC.getCopyOfContextMap();

            // 日志跟踪信息
            if (map.get(LogTraceConstant.ORIGIN_IP) != null){
                requestTemplate.header(LogTraceConstant.ORIGIN_IP, map.get(LogTraceConstant.ORIGIN_IP));
            }
            if (map.get(LogTraceConstant.UPSTREAM_IP) != null){
                requestTemplate.header(LogTraceConstant.UPSTREAM_IP, map.get(LogTraceConstant.UPSTREAM_IP));
            }
            if (map.get(LogTraceConstant.TRACE_ID) != null){
                requestTemplate.header(LogTraceConstant.TRACE_ID, map.get(LogTraceConstant.TRACE_ID));
            }
            if (map.get(LogTraceConstant.SPAN_ID) != null){
                requestTemplate.header(LogTraceConstant.SPAN_ID, map.get(LogTraceConstant.SPAN_ID));
            }

            if (map.get(LogTraceConstant.SPAN_ID) != null){
                requestTemplate.header(LogTraceConstant.SPAN_ID, map.get(LogTraceConstant.SPAN_ID));
            }

            // 用户跟踪信息
            if (map.get(LogTraceConstant.TENANT_ID) != null){
                requestTemplate.header(LogTraceConstant.TENANT_ID, map.get(LogTraceConstant.TENANT_ID));
            }
            if (map.get(LogTraceConstant.AUTH_ID) != null){
                requestTemplate.header(LogTraceConstant.AUTH_ID, map.get(LogTraceConstant.AUTH_ID));
            }
            if (map.get(LogTraceConstant.USER_ID) != null){
                requestTemplate.header(LogTraceConstant.USER_ID, map.get(LogTraceConstant.USER_ID));
            }
        };
    }
}
