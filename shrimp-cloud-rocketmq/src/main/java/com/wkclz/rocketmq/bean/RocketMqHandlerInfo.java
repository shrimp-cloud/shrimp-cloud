package com.wkclz.rocketmq.bean;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class RocketMqHandlerInfo {

    private String topic;
    private String tag;
    private Object bean;
    private Method method;
}
