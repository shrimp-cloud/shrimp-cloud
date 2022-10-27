package com.wkclz.rocketmq.producer;

import org.springframework.stereotype.Component;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Component
// @ConditionalOnBean(RocketMqClient.class)
public class RocketMqProducer {


    public void send(String topic, String tag, String key) {
        // producer.send(message);
    }

}
