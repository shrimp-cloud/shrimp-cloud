package com.wkclz.server.config;

import com.wkclz.server.handler.interfaces.ExecuteData;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * netty配置信息类
 */
@Component
@Data
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
