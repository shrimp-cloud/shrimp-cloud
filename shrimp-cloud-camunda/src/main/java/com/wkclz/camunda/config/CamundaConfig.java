package com.wkclz.camunda.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class CamundaConfig {

    @Value("${camunda.auth.username:admin}")
    private String username;
    @Value("${camunda.auth.password:password}")
    private String password;

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return template -> {
            String auth = username + ":" + password;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            template.header("token", encodedAuth);
        };
    }

}
