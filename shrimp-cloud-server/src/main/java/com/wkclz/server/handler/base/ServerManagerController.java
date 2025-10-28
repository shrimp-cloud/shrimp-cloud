package com.wkclz.server.handler.base;

import com.wkclz.server.handler.tcp.TcpDataHandler;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/serverManager")
@ConditionalOnExpression("${netty.isManager:false}")
public class ServerManagerController{
    @Resource
    private ServerManager serverManager;
    @Resource
    private TcpDataHandler tcpDataHandler;

    @RequestMapping("/stop")
    public Object stop(){
        serverManager.stop();
        return Map.of("message","完成");
    }

    @RequestMapping("/run")
    public Object run(){
        serverManager.run();
        return Map.of("message","完成");
    }

    @RequestMapping("/status")
    public Object status(){
        return Map.of("status",serverManager.status());
    }

    @GetMapping("/writeData")
    public Object writeData(String id,String data){
        tcpDataHandler.writeData(id,data.getBytes());
        return Map.of("message","success");
    }

}
