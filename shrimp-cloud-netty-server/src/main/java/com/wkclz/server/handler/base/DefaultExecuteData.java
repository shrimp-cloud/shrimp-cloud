package com.wkclz.server.handler.base;

import cn.hutool.extra.spring.SpringUtil;
import com.wkclz.server.handler.log.LogPlug;
import com.wkclz.server.bean.ReceiveData;
import com.wkclz.server.handler.interfaces.ExecuteData;
import com.wkclz.server.util.NettyHandlerMethodUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component("default")
public class DefaultExecuteData implements ExecuteData {

    @Autowired
    private LogPlug logPlug;
    @Override
    public void invoke(ReceiveData r) {
        handler(r.getCtx(),r);
        logPlug.writeLog(true,r);
    }

    private void handler(ChannelHandlerContext ctx, ReceiveData receiveData) {
        List list = SpringUtil.getBean(NettyHandlerMethodUtil.class).invoke(receiveData.getReqData());
        try {
            receiveData.success(write(ctx,list));
        } catch (Exception e) {
            receiveData.error(e.getMessage());
        }
    }

    private byte[] write(ChannelHandlerContext ctx,List list) throws Exception{
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for(Object o:list){
            output.write((byte[])o);
        }
        ByteBuf buf = Unpooled.copiedBuffer(output.toByteArray());
        ctx.channel().writeAndFlush(buf);
        return output.toByteArray();
    }
}
