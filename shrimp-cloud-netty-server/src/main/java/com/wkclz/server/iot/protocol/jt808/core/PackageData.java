package com.wkclz.server.iot.protocol.jt808.core;
import lombok.Data;

import java.nio.channels.Channel;

@Data
public class PackageData {

    /**
     * 16byte 消息头
     */
    protected MsgHeader msgHeader;

    // 消息体字节数组
    protected byte[] msgBodyBytes;

    /**
     * 校验码 1byte
     */
    protected int checkSum;

    //记录每个客户端的channel,以便下发信息给客户端
    protected Channel channel;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

}