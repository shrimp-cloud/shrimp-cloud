package com.wkclz.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * netty配置信息类
 */
@Data
@Configuration
public class NettyConfig {

    @Value("${netty.nettyPort}")
    private int nettyPort;

    @Value("${netty.isWriteLog}")
    private boolean isWriteLog;

    @Value("${netty.appName}")
    private String appName;

    @Value("${netty.serverType}")
    private String serverType;

    @Value("${netty.capacity}")
    private int capacity;

    @Value("${netty.executeDataName}")
    private String executeDataName;

}
