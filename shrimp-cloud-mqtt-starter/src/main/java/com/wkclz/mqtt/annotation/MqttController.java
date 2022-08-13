package com.wkclz.mqtt.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * MqttController 在类上使用，其中parentTopic值为需要监听的1级Topic，其中使用
 * Component 可以使其注解的类实例化为为Bean对象放入到Spring容器中，基于此才能在利用BeanPostProcessor中获得其对象。
 * @author wangkaicun
 * @date 2022-04-08
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MqttController {

    /**
     * 监听的父topic
     *
     * @return 监听的父topic
     */
    String value();
}