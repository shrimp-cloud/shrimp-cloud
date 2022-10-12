package com.wkclz.mqtt.client;

import com.wkclz.mqtt.enums.Qos;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Component
@ConditionalOnBean(MqttClient.class)
public class MqttProducer {

    @Autowired
    private MqttClient mqttClient;

    public void send(String topic, byte[] msg) {
        send(topic, Qos.QOS_1, msg);
    }

    public void send(String topic, Qos qos, byte[] msg) {
        MqttMessage message = new MqttMessage(msg);
        message.setQos(qos.getValue());
        try {
            mqttClient.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
