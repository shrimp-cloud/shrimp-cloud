package com.wkclz.rocketmq.exception;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public class RocketMqTimeoutException extends RocketMqRemoteException {

	public RocketMqTimeoutException() {
		super();
	}

	public RocketMqTimeoutException(String message) {
		super(message);
	}
}
