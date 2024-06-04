package com.wkclz.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Configuration
// @ConditionalOnBean(MqttClient.class)
public class MqttApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(MqttApplicationListener.class);

    @Autowired(required = false)
    private MqttAsyncClient mqttAsyncClient;

    @Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
        if (mqttAsyncClient == null) {
            return;
        }
        // Spring是父容器，SpringMVC是其子容器，只需要在父容器触发时处理逻辑即可
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        MqttSubcribe.subscribeTopics(mqttAsyncClient);
		logger.info("MqConsumer Started");
	}

}
