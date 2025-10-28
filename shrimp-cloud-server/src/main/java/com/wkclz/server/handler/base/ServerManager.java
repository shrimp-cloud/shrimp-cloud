package com.wkclz.server.handler.base;

import cn.hutool.core.thread.ThreadUtil;
import com.wkclz.server.config.NettyConfig;
import com.wkclz.server.handler.interfaces.DataServer;
import com.wkclz.server.handler.log.LogPlug;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServerManager implements DataServer{
    @Resource
    Map<String, DataServer> dataServerMap;
    @Resource
    private NettyConfig nettyConfig;
    @Resource
    private LogPlug logPlug;

    @Override
    public void run() {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                logPlug.writeLog(false,"server线程启动");
                dataServerMap.get(nettyConfig.getServerType()).run();
                logPlug.writeLog(false,"server线程退出");
            }
        });
    }

    @Override
    public void stop() {
        dataServerMap.get(nettyConfig.getServerType()).stop();
    }

    @Override
    public String status() {
        return dataServerMap.get(nettyConfig.getServerType()).status();
    }
}
