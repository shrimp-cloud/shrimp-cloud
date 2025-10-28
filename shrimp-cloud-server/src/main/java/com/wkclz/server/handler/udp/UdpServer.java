package com.wkclz.server.handler.udp;

import com.wkclz.server.config.NettyConfig;
import com.wkclz.server.handler.interfaces.DataServer;
import com.wkclz.server.handler.log.LogPlug;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component("udp")
public class UdpServer implements DataServer {
    @Resource
    private LogPlug logPlug;
    @Resource
    private UdpDataHandler udpDataHandler;
    @Resource
    private NettyConfig nettyConfig;
    private String status;
    EventLoopGroup bossGroup;
    EpollEventLoopGroup epollBossGroup;

    @Override
    public void run() {
        try {
            run(nettyConfig.getNettyPort());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        if(bossGroup!=null) {
            bossGroup.shutdownGracefully();
        }
        if(epollBossGroup!=null) {
            epollBossGroup.shutdownGracefully();
        }
        status = "stop";
    }

    @Override
    public String status() {
        return status;
    }

    public void run(int port) throws Exception {
        String osName = System.getProperty("os.name").toLowerCase();
        logPlug.writeLog(false,"当前操作系统是"+ osName);
        logPlug.writeLog(false,"准备运行端口：" + port);
        if (osName.contains("linux")) {
            linuxInitRun(port);
        } else {
            windowsInitRun(port);
        }
    }

    private void windowsInitRun(int port) throws Exception{
        bossGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(udpDataHandler);
                    }
                });
            status = "run";
            //绑定端口，同步等待成功
            ChannelFuture f = bootstrap.bind(port).sync();
            //等待服务监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //退出，释放线程资源
            bossGroup.shutdownGracefully();
            status = "stop";
        }
    }

    private void linuxInitRun(int port) throws Exception{
        epollBossGroup = new EpollEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(epollBossGroup)
                .channel(EpollDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(udpDataHandler);
                    }
                });
            status = "run";
            //绑定端口，同步等待成功
            ChannelFuture f = bootstrap.bind(port).sync();
            //等待服务监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //退出，释放线程资源
            epollBossGroup.shutdownGracefully();
            status = "stop";
        }
    }

}
