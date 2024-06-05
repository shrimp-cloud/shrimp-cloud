package com.wkclz.mqtt.enums;


/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public enum Qos {

    /**
     * QOS0
     */
    QOS_0(0, "cleanSession=true 无离线消息，在线消息只尝试推一次 cleanSession=false 无离线消息，在线消息只尝试推一次"),

    /**
     * QOS1
     */
    QOS_1(1, "cleanSession=true 无离线消息，在线消息保证可达 cleanSession=false 有离线消息，所有消息保证可达"),

    /**
     * QOS2
     */
    QOS_2(2, "cleanSession=true 无离线消息，在线消息保证只推一次 cleanSession=false 暂不支持");

    private Integer value;
    private String label;

    Qos(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

}
