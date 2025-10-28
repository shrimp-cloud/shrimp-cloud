package com.wkclz.server.handler.tcp;

import com.wkclz.server.config.NettyConfig;
import com.wkclz.server.handler.interfaces.DataServer;
import com.wkclz.server.handler.log.LogPlug;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * netty服务类（核心）
 */
@Component("tcp")
public class TcpServer implements DataServer {
    @Resource
    private LogPlug logPlug;
    @Resource
    private TcpDataHandler serverHandler;
    @Resource
    private NettyConfig nettyConfig;
    private String status;
    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;
    EpollEventLoopGroup epollBossGroup;
    EpollEventLoopGroup epollWorkerGroup;

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
        //退出，释放线程资源
        if(workerGroup!=null) {
            workerGroup.shutdownGracefully();
        }
        if(bossGroup!=null) {
            bossGroup.shutdownGracefully();
        }
        if(epollBossGroup!=null) {
            epollBossGroup.shutdownGracefully();
        }
        if(epollWorkerGroup!=null) {
            epollWorkerGroup.shutdownGracefully();
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
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childHandler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        channel.pipeline().addLast(serverHandler);
                    }
                });
            status = "run";
            //绑定端口，同步等待成功
            ChannelFuture f = bootstrap.bind(port).sync();
            //等待服务监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            //退出，释放线程资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            status = "stop";
        }
    }

    private void linuxInitRun(int port) throws Exception{
        epollBossGroup = new EpollEventLoopGroup();
        epollWorkerGroup = new EpollEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(epollBossGroup, epollWorkerGroup)
                .channel(EpollServerSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048))
                .option(EpollChannelOption.SO_REUSEPORT, true)
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(2048))
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT).childHandler(new ChannelInitializer(){
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(serverHandler);
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
            epollWorkerGroup.shutdownGracefully();
            status = "stop";
        }
    }

}
