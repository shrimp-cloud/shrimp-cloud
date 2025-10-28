package com.wkclz.server.bean;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import lombok.Builder;
import lombok.Data;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 服务端信息
 */
@Data
@Builder
public class ServerDto implements java.io.Serializable{

    /**
     * ip地址
     */
    private String serverIp;

    /**
     * 端口号
     */
    private int serverPort;

    @Override
    public String toString() {
        return "ServerDto{" +
            "serverIp='" + serverIp + '\'' +
            ", serverPort=" + serverPort +
            '}';
    }
    public static ServerDto create(ChannelHandlerContext ctx){
        try {
            SocketChannel channel = (SocketChannel) ctx.channel();
            String serverIp = channel.localAddress().getAddress().getHostAddress();
            int serverPort = channel.localAddress().getPort();
            return ServerDto.builder().serverIp(serverIp).serverPort(serverPort).build();
        }catch(Exception e){
            return ServerDto.builder().serverIp("").serverPort(0).build();
        }
    }

}
