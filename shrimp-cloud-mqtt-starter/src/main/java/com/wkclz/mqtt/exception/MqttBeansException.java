package com.wkclz.mqtt.exception;

import org.springframework.beans.BeansException;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public class MqttBeansException extends BeansException {

	public MqttBeansException(String msg) {
		super(msg);
	}

	public MqttBeansException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
