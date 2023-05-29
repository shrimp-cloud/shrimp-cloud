package com.wkclz.mqtt.demo;

import com.wkclz.mqtt.client.MqttProducer;
import com.wkclz.mqtt.config.MqttConfig;
import com.wkclz.mqtt.enums.Qos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MqttProducerDemo {

    @Autowired
    private MqttConfig mqttConfig;
    @Autowired
    private MqttProducer mqttProducer;

    @Scheduled(fixedDelay = 12_000)
    public void keepaliveBreath() {
        Integer task = mqttConfig.getKeepAliveTask();
        if (task == null || task != 1) {
            return;
        }
        Map<String, Long> map = new HashMap<>();
        map.put("now", System.currentTimeMillis());
        mqttProducer.send("keepalive/breath", map, Qos.QOS_0);
    }

}