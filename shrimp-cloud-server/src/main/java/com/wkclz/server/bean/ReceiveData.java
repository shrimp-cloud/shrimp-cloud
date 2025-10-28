package com.wkclz.server.bean;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.IdUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

/**
 * 接收数据对象
 */
@Data
@Builder
public class ReceiveData implements java.io.Serializable{

    /**
     * 流水id（日期+uid）
     */
    private String id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 处理状态
     */
    private String handleStatus;

    /**
     *  请求时间
     */
    private Date reqTime;

    /**
     * 请求数据
     */
    private byte[] reqData;

    /**
     *  处理时间
     */
    private Date resTime;

    /**
     *  返回数据
     */
    private byte[] resData;

    /**
     *  耗时
     */
    private long duration;

    /**
     *  客户端信息
     */
    private ClientDto clientDto;

    /**
     *  服务端信息
     */
    private ServerDto serverDto;

    private String reqStr;

    private String resStr;

    private ChannelHandlerContext ctx;

    @Override
    public String toString() {
        return "ReceiveData{" +
            "appName='" + appName + '\'' +
            ", handleStatus='" + handleStatus + '\'' +
            ", id='" + id + '\'' +
            ", reqTime=" + reqTime +
            ", reqData=" + Arrays.toString(reqData) +
            ", resTime=" + resTime +
            ", resData=" + Arrays.toString(resData) +
            ", duration=" + duration +
            ", clientDto=" + clientDto +
            ", serverDto=" + serverDto +
            '}';
    }

    public static ReceiveData create(String appName, byte[] data, ClientDto clientDto, ServerDto serverDto,ChannelHandlerContext ctx){
        return ReceiveData.builder()
            .clientDto(clientDto)
            .serverDto(serverDto)
            .id(IdUtil.simpleUUID())
            .appName(appName)
            .handleStatus("success")
            .reqData(data)
            .reqStr(HexUtil.encodeHex(data).toString())
            .reqTime(new Date())
            .ctx(ctx)
            .build();
    }

    public void success(byte[] data){
        this.resData = data;
        this.resTime = new Date();
        this.resStr = HexUtil.encodeHex(data).toString();
        this.duration = this.resTime.getTime() - this.reqTime.getTime();
    }

    public void error(String errorInfo){
        this.handleStatus = errorInfo;
    }

}
