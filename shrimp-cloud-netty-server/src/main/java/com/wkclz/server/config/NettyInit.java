package com.wkclz.server.config;

import cn.hutool.core.thread.ThreadUtil;
import com.wkclz.server.handler.base.ServerManager;
import com.wkclz.server.handler.interfaces.DataServer;
import com.wkclz.server.handler.tcp.TcpServer;
import com.wkclz.server.handler.udp.UdpServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 启动类
 */
@Component
public class NettyInit {
    @Resource
    private ServerManager serverManager;

    @PostConstruct
    void init(){
        serverManager.run();
    }

}
