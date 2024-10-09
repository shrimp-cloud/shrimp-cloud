package com.wkclz.rocketmq.exception;

import org.springframework.beans.BeansException;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public class RocketMqBeansException extends BeansException {

    public RocketMqBeansException(String msg) {
        super(msg);
    }

    public RocketMqBeansException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
