package com.wkclz.server.handler.tcp;

import com.wkclz.server.bean.ClientDto;
import com.wkclz.server.bean.ReceiveData;
import com.wkclz.server.bean.ServerDto;
import com.wkclz.server.config.NettyConfig;
import com.wkclz.server.handler.interfaces.ExecuteData;
import com.wkclz.server.handler.log.LogPlug;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据接收处理类
 */
@Component
@ChannelHandler.Sharable
public class TcpDataHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Autowired
    private NettyConfig nettyConfig;
    @Autowired
    private Map<String,ExecuteData> executeDataMap;
    @Autowired
    LogPlug logPlug;
    Map<String,ChannelHandlerContext> session = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        int length = byteBuf.readableBytes();
        if(length>0){
            byte[] b = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(b);
            executeDataMap.get(nettyConfig.getExecuteDataName()).invoke(ReceiveData.create(nettyConfig.getAppName(),b,
                ClientDto.create(ctx),ServerDto.create(ctx),ctx));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        session.put(ctx.channel().id().asLongText(),ctx);
        logPlug.writeLog(false,"创建连接:"+ctx.channel().id().asLongText());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        session.remove(ctx.channel().id().asLongText());
        logPlug.writeLog(false,"断开连接:"+ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

    public void writeData(String id,byte[] b){
        ChannelHandlerContext ctx = session.get(id);
        ByteBuf buf = Unpooled.copiedBuffer(b);
        ctx.channel().writeAndFlush(buf);
    }

}
