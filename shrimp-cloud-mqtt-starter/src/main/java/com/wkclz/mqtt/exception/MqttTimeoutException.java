package com.wkclz.mqtt.exception;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public class MqttTimeoutException extends MqttRemoteException {

	public MqttTimeoutException() {
		super();
	}

	public MqttTimeoutException(String message) {
		super(message);
	}
}
