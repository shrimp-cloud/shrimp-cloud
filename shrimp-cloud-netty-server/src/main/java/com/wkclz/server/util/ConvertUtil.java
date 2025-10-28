package com.wkclz.server.util;

import com.wkclz.server.config.ModuleConstant;

/**
 * 基础类型数据转换工具类
 */
public final class ConvertUtil {

    /**
     * 单例对象，用于反射
     */
    public static ConvertUtil SINGLETON = new ConvertUtil();

    /**
     *  字节数组转换为字符串
     */
    public static String byte_string(byte[] b){
        return new String(b);
    }

    /**
     * 字节数组转换为中文字符串
     */
    public static String byte_string_cn(byte[] b) throws Exception{
        return new String(b, ModuleConstant.CHARSET_NAME);
    }

    /**
     * 字符串转换为字节数组
     */
    public static byte[] string_byte(String s){
        return s.getBytes();
    }

    /**
     * 字符串转换为中文字节数组
     */
    public static byte[] string_byte_cn(String s) throws Exception{
        return s.getBytes(ModuleConstant.CHARSET_NAME);
    }

}
