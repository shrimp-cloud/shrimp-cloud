package com.wkclz.rocketmq.annotation;

import java.lang.annotation.*;

/**
 * MqttTopicMapping 在方法上使用，其中subTopic的值为需要订阅的子Topic，与1级Topic共同组成MQTT的Topic
 * @author wangkaicun
 * @date 2022-04-08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMqTagMapping {

    /**
     * 筛选 Topic 下的 tag
     *
     * @return tag
     */
    String value() default "";
}