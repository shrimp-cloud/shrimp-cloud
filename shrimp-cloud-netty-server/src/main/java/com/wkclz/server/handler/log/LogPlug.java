package com.wkclz.server.handler.log;

import com.alibaba.fastjson2.JSONObject;
import com.wkclz.server.config.NettyConfig;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 日志插件
 */

@Component
public final class LogPlug {
    @Resource
    private NettyConfig nettyConfig;
    private static final Logger logger = LoggerFactory.getLogger(LogPlug.class);
    public void writeLog(boolean isJson,Object obj){
        if(nettyConfig.isWriteLog()){
            if(isJson) {
                logger.info(JSONObject.toJSONString(obj));
            }else{
                logger.info(obj.toString());
            }
        }
    }

}
