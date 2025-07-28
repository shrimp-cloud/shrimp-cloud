package com.wkclz.mybatis.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class DefaultDataSourceConfig {
    @Value("${spring.datasource.name:default}")
    private volatile String name;
    @Value("${spring.datasource.username:}")
    private volatile String username;
    @Value("${spring.datasource.password:}")
    private volatile String password;
    @Value("${spring.datasource.url:}")
    private volatile String url;
    @Value("${spring.datasource.driverClassName:}")
    private volatile String driverClassName;
    @Value("${spring.datasource.druid.initialSize:0}")
    private volatile String initialSize;
    @Value("${spring.datasource.druid.maxActive:8}")
    private volatile String maxActive;
    @Value("${spring.datasource.druid.minIdle:0}")
    private volatile String minIdle;
    @Value("${spring.datasource.druid.maxIdle:8}")
    private volatile String maxIdle;
    @Value("${spring.datasource.druid.maxWait:-1}")
    private volatile String maxWait;
    @Value("${spring.datasource.druid.filters:stat,wall,log4j}")
    private volatile String filters;

}
