package com.wkclz.auth.interceptor;

import com.wkclz.auth.interceptor.handler.LogTraceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description: 微服务场景，使用 网关完成了对应的功能，非微服务场景，使用 Filter 完成了功能，此处不再需要
 * Created: wangkaicun @ 2017-10-18 下午11:45
 */

// @Configuration
public class AppInterceptor implements WebMvcConfigurer {

    @Autowired
    private LogTraceHandler logTraceHandler;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logTraceHandler).addPathPatterns("/**");
    }

}
