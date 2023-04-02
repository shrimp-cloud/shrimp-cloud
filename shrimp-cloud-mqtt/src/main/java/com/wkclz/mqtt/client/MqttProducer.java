package com.wkclz.mqtt.client;

import com.alibaba.fastjson2.JSONObject;
import com.wkclz.mqtt.enums.Qos;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Component
// @ConditionalOnBean(MqttClient.class)
public class MqttProducer {

    @Autowired
    private MqttClient mqttClient;

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
