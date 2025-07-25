package com.wkclz.mqtt.exception;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public class MqttRemoteException extends RuntimeException {

    public MqttRemoteException() {
        super();
    }

    public MqttRemoteException(String message) {
        super(message);
    }

    public static MqttRemoteException error(String msg) {
        return new MqttRemoteException(msg);
    }

}
