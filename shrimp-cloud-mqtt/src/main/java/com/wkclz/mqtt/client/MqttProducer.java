package com.wkclz.mqtt.client;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson2.JSONObject;
import com.wkclz.mqtt.enums.Qos;
import com.wkclz.mqtt.exception.MqttBeansException;
import com.wkclz.mqtt.exception.MqttSendException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Slf4j
@Component
public class MqttProducer {

    @Autowired(required = false)
    private MqttAsyncClient mqttAsyncClient;


    public void sendDelay(String topic, String msg, Integer delay) {
        sendDelay(topic, Arrays.asList(msg), delay, Qos.QOS_1);
    }
    public void sendDelay(String topic, String msg, Qos qos) {
        sendDelay(topic, Arrays.asList(msg), 500, qos);
    }
    public void sendDelay(String topic, String msg, Integer delay, Qos qos) {
        sendDelay(topic, Arrays.asList(msg), delay, qos);
    }

    public void sendDelay(String topic, List<String> msgs, Integer delay) {
        sendDelay(topic, msgs, delay, Qos.QOS_1);
    }
    public void sendDelay(String topic, List<String> msgs, Qos qos) {
        sendDelay(topic, msgs, 500, qos);
    }
    public void sendDelay(String topic, List<String> msgs, Integer delay, Qos qos) {
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
        int finalDelay = delay;
        try {
            ThreadPoolExecutor executor = ThreadUtil.newExecutor();
            executor.execute(() -> {
                for (String msg : msgs) {
                    try {
                        Thread.sleep(finalDelay);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw MqttSendException.error(e.getMessage());
                    }
                    log.info("mqtt sent msg, topic:{}, message: {}", topic, msg);
                    byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                    sendMsg(topic, bytes, finalQos);
                }
            });
        } catch (Exception e) {
            //
        }
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
        log.info("mqtt sent msg, topic:{}, message: {}", topic, json);
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
        if (mqttAsyncClient == null) {
            throw new MqttBeansException("mqtt is disabled!");
        }
        MqttMessage message = new MqttMessage(msg);
        message.setQos(qos.getValue());
        try {
            mqttAsyncClient.publish(topic, message);
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }
    }

}
