package com.wkclz.spring.helper;

import com.wkclz.common.utils.SnowflakeIdWorker;
import com.wkclz.spring.config.Sys;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SnowflakeHelper {

    private static SnowflakeIdWorker SFIW = null;

    // 生成唯一序列
    public static synchronized long getSnowflakeId() {
        if (SFIW == null) {
            long workId = SnowflakeHelper.getWorkId();
            long datacenterId = SnowflakeHelper.getDatacenterId();
            SFIW = new SnowflakeIdWorker(workId, datacenterId);
        }
        return SFIW.nextId();
    }

    /**
     * 获取机器编码
     */
    private static long getWorkId() {
        long machinePiece;
        StringBuilder sb = new StringBuilder();
        Enumeration<NetworkInterface> e = null;
        try {
            e = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            sb.append(ni.toString());
        }
        machinePiece = sb.toString().hashCode();
        return machinePiece;
    }

    private static long getDatacenterId() {
        int hashCode = Sys.CURRENT_ENV.hashCode();
        return hashCode;
    }

    /*
    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            long snowflakeId = SnowflakeHelper.getSnowflakeId();
            System.out.println(snowflakeId);
        }
    }
    */


}
