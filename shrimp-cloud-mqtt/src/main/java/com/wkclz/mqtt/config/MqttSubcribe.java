package com.wkclz.mqtt.config;

import com.wkclz.mqtt.bean.MqttHexMsg;
import com.wkclz.mqtt.handler.MqttHandlerFactory;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */

public class MqttSubcribe {

    private static final Logger logger = LoggerFactory.getLogger(MqttSubcribe.class);

    public static void subscribeTopics(MqttAsyncClient mqttClient) {
        Set<String> parentTopicSet = MqttHandlerFactory.getParentTopicSet();
        if (parentTopicSet.isEmpty()) {
            logger.warn("当前应用并未有任何topic订阅");
            return;
        }

        //订阅所有 parentTopic，再根据 分发
        parentTopicSet.forEach(parentTopic -> {
            logger.info("Add a new mq subscription,parent topic:{}", parentTopic);
            try {
                String subscribeTopic = parentTopic + "/#";
                mqttClient.subscribe(subscribeTopic, 1, new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String topic, MqttMessage message) {
                        messageArrivedHandle(topic, message);
                    }
                });
            } catch (MqttException e) {
                logger.error(e.getMessage(), e);
            }
        });
        logger.info("MqConsumer subscribe {} topic(s)", parentTopicSet.size());
    }

    private static void messageArrivedHandle(String topic, MqttMessage context) {
        logger.debug("mqtt topic {} receive message: {}", topic, context);
        String topicMappingKey = topic;
        // 优先查找全 topic 配置，再查找只 mapping 了父类的方法
        Method method = MqttHandlerFactory.getMqttHandler(topic);
        if (method == null) {
            int i = topic.indexOf("/");
            if (i > 0) {
                topicMappingKey = topic.substring(0, i) + "/#";
                method = MqttHandlerFactory.getMqttHandler(topicMappingKey);
            }
        }
        if (null == method) {
            logger.warn("当前没有处理该topic的handler,topic:{}", topic);
            return;
        }

        // 获得mqtt的一些数据
        int splitIndex = topic.indexOf("/");
        MqttHexMsg msg = new MqttHexMsg();
        msg.setTopic(topic);
        msg.setParentTopic(splitIndex > 0 ? topic.substring(0, splitIndex) : topic);
        msg.setSubTopic(splitIndex > 0 ? topic.substring(splitIndex + 1) : "#");
        msg.setId(context.getId());
        msg.setQos(context.getQos());
        msg.setPayload(context.getPayload());

        // 处理入参
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == MqttHexMsg.class) {
                paramValues[i] = msg;
                continue;
            }
            paramValues[i] = null;
        }
        try {
            method.invoke(MqttHandlerFactory.getMqttController(topicMappingKey), paramValues);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
