package com.wkclz.server.bean;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;
import lombok.Data;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 客户端信息
 */
@Data
@Builder
public class ClientDto implements java.io.Serializable {

    /**
     * ip地址
     */
    private String ip;

    /**
     * 端口号
     */
    private int port;

    public static ClientDto create(ChannelHandlerContext ctx){
        // 获取Channel
        Channel channel = ctx.channel();
        // 获取远程地址（即客户端地址）
        SocketAddress remoteAddress = channel.remoteAddress();
        // 转换为InetSocketAddress，以便获取IP地址
        InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;

        if(inetSocketAddress!=null){
            // 获取IP地址
            String clientIp = inetSocketAddress.getAddress().getHostAddress();
            return ClientDto.builder().ip(clientIp).port(inetSocketAddress.getPort()).build();
        }else{
            return ClientDto.builder().ip("").port(0).build();
        }
    }

    @Override
    public String toString() {
        return "ClientDto{" +
            "ip='" + ip + '\'' +
            ", port=" + port +
            '}';
    }
}
