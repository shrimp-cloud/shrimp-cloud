package com.wkclz.mqtt.client;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
import com.wkclz.mqtt.enums.Qos;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Slf4j
@Component
// @ConditionalOnBean(MqttClient.class)
public class MqttProducer {

    @Autowired
    private MqttClient mqttClient;

    public void send(String topic, List<String> msgs, Integer delay) {
        send(topic, msgs, delay, Qos.QOS_1);
    }
    public void send(String topic, List<String> msgs, Qos qos) {
        send(topic, msgs, 500, qos);
    }
    public void send(String topic, List<String> msgs, Integer delay, Qos qos) {
        if (topic == null || CollectionUtils.isEmpty(msgs)) {
            return;
        }
        if (delay == null) {
            delay = 500;
        }
        if (qos == null) {
            qos = Qos.QOS_1;
        }
        Qos finalQos = qos;
        Integer finalDelay = delay;
        ThreadUtil.newExecutor().execute(() -> {
            for (String msg : msgs) {
                log.info("mqtt sent msg, tipic:{}, message: {}", topic, msg);
                byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                sendMsg(topic, bytes, finalQos);
                try {
                    Thread.sleep(finalDelay);
                } catch (InterruptedException e) {
                    //
                }
            }
        });
    }

    public void send(String topic, Object msg) {
        if (msg == null) {
            return;
        }
        send(topic, msg, Qos.QOS_1);
    }

    public void send(String topic, Object msg, Qos qos) {
        if (msg == null) {
            return;
        }
        String json = JSONObject.toJSONString(msg);
        log.info("mqtt sent msg, tipic:{}, message: {}", topic, json);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        sendMsg(topic, bytes, qos);
    }

    public void send(String topic, byte[] msg) {
        if (msg == null) {
            return;
        }
        send(topic, msg, Qos.QOS_1);
    }

    public void send(String topic, byte[] msg, Qos qos) {
        if (msg == null) {
            return;
        }
        sendMsg(topic, msg, qos);
    }

    private void sendMsg(String topic, byte[] msg, Qos qos) {
        MqttMessage message = new MqttMessage(msg);
        message.setQos(qos.getValue());
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
