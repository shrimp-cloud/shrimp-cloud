package com.wkclz.rocketmq.test;

import com.wkclz.rocketmq.annotation.RocketMqTag;
import com.wkclz.rocketmq.annotation.RocketMqTopic;

@RocketMqTopic("demo-topic")
public class CunsumerTopic {

    @RocketMqTag
    public void cunsumerTag(Object msg) {

    }
}
