package com.wkclz.mqtt.exception;

import org.springframework.beans.BeansException;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public class MqttSendException extends BeansException {

    public MqttSendException(String msg) {
        super(msg);
    }

    public MqttSendException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public static MqttSendException error(String msg) {
        return new MqttSendException(msg);
    }

}
