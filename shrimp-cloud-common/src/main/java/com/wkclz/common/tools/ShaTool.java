package com.wkclz.common.tools;

import com.wkclz.common.exception.BizException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class ShaTool {

    private final static List<String> ALGORITHMS = Arrays.asList("SHA-1", "SHA-256", "SHA-384", "SHA-512");

    public static String sha1(String input) {
        return sha(input, "SHA-1");
    }
    public static String sha256(String input) {
        return sha(input, "SHA-256");
    }
    public static String sha384(String input) {
        return sha(input, "SHA-384");
    }
    public static String sha512(String input) {
        return sha(input, "SHA-512");
    }

    public static String sha(String input, String algorithm) {
        if (algorithm == null) {
            throw BizException.error("algorithm can nnot be null");
        }
        if (!ALGORITHMS.contains(algorithm)) {
            throw BizException.error("algorithm is error");
        }
        try {
            // 创建MessageDigest实例，指定SHA-256算法
            // SHA-1、SHA-256、SHA-384、SHA-512
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            // 对输入字符串进行编码，得到字节数组
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            // 将字节数组转换为十六进制字符串
            return toHexString(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHexString(byte[] bytes) {
        // 将字节数组转换为十六进制字符串的辅助方法
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
