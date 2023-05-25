package com.wkclz.mqtt.demo;

import com.alibaba.fastjson2.JSONObject;
import com.wkclz.mqtt.annotation.MqttController;
import com.wkclz.mqtt.annotation.MqttTopicMapping;
import com.wkclz.mqtt.bean.MqttHexMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MqttController("keepalive")
public class MqttConsumerDemo {

    private static final Logger logger = LoggerFactory.getLogger(MqttConsumerDemo.class);

    @MqttTopicMapping("breath")
    public void breath(MqttHexMsg msg) {
        logger.debug("breath message: {}", JSONObject.toJSONString(msg));
    }

}