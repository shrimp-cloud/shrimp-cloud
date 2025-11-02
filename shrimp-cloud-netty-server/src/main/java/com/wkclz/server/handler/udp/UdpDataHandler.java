package com.wkclz.server.handler.udp;

import com.wkclz.server.bean.ClientDto;
import com.wkclz.server.bean.ReceiveData;
import com.wkclz.server.bean.ServerDto;
import com.wkclz.server.config.NettyConfig;
import com.wkclz.server.handler.interfaces.ExecuteData;
import com.wkclz.server.handler.log.LogPlug;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数据接收处理类
 */
@Component
@ChannelHandler.Sharable
public class UdpDataHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Resource
    private NettyConfig nettyConfig;
    @Resource
    private Map<String,ExecuteData> executeDataMap;
    @Resource
    private LogPlug logPlug;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) {
        int length = packet.content().readableBytes();
        if(length>0){
            byte[] b = new byte[packet.content().readableBytes()];
            packet.content().readBytes(b);
            executeDataMap.get(nettyConfig.getExecuteDataName()).invoke(ReceiveData.create(nettyConfig.getAppName(),b,
                ClientDto.create(ctx),ServerDto.create(ctx),ctx));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        logPlug.writeLog(false,"创建连接");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logPlug.writeLog(false,"断开连接");
        super.channelInactive(ctx);
    }

}
