package com.wkclz.mqtt.annotation;

import java.lang.annotation.*;

/**
 * MqttTopicMapping 在方法上使用，其中subTopic的值为需要订阅的子Topic，与1级Topic共同组成MQTT的Topic
 * @author wangkaicun
 * @date 2022-04-08
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqttTopicMapping {

    /**
     * 订阅的子topic，默认可以只订阅1级topic
     *
     * @return 订阅的子topic
     */
    String value() default "";
}