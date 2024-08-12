package com.wkclz.common.tools;

import com.wkclz.common.exception.BizException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Tool {


    public static String md5lowerCase32(String str) {
        return md5(str);
    }

    public static String md5UpperCase32(String str) {
        String s = md5(str);
        return s.toUpperCase();
    }

    public static String md5lowerCase16(String str) {
        String s = md5(str);
        return s.substring(8, 24);
    }

    public static String md5UpperCase16(String str) {
        String s = md5(str);
        return s.substring(8, 24).toUpperCase();
    }


    /**
     * String 2 MD5
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        if (str == null || "".equals(str)) {
            throw BizException.error("md5 内容不能为空");
        }
        byte[] secretBytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        StringBuilder md5code = new StringBuilder(new BigInteger(1, secretBytes).toString(16));
        int x = 32 - md5code.length();
        for (int i = 0; i < x; i++) {
            md5code.insert(0, "0");
        }
        return md5code.toString();
    }




}
