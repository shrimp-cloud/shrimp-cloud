package com.wkclz.server.handler.interfaces;

/**
 * 服务实现类
 */
public interface DataServer {

    /**
     * 启动服务
     */
    void run();

    /**
     * 停止服务
     */
    void stop();

    /**
     *  查看服务状态
     */
    String status();

}
