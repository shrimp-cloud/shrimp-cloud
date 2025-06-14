package com.wkclz.common.tools;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.wkclz.common.exception.BizException;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaTool {

    public static String[] genKeyPair() {
        return genKeyPair(1024);
    }

    public static String[] genKeyPair(Integer keySize) {
        if (keySize == null) {
            throw BizException.error("keySize can not be null");
        }

        if (keySize != 1024 && keySize != 2048 && keySize != 4096) {
            throw BizException.error("keySize 必须是 1024、2048、4096 中的一个");
        }

        try {
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA", "SunRsaSign");
            gen.initialize(keySize, new SecureRandom());
            KeyPair pair = gen.generateKeyPair();

            byte[] privateEncoded = pair.getPrivate().getEncoded();
            byte[] publicEncoded = pair.getPublic().getEncoded();

            String privateKey = Base64Tool.base64Encode(privateEncoded);
            String publicKey = Base64Tool.base64Encode(publicEncoded);

            String[] keyPair = new String[2];
            keyPair[0] = privateKey;
            keyPair[1] = publicKey;
            return keyPair;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptByPrivateKey(String input, String rsaPrivateKey) {
        RSA rsa = SecureUtil.rsa(rsaPrivateKey, null);
        byte[] encrypt = rsa.encrypt(input, KeyType.PrivateKey);
        return Base64Tool.base64Encode(encrypt);
    }

    public static String decryptByPublicKey(String input, String rsaPublicKey) {
        RSA rsa = SecureUtil.rsa(null, rsaPublicKey);
        byte[] decrypt = rsa.decrypt(input, KeyType.PublicKey);
        return new String(decrypt);
    }

    public static String encryptByPublicKey(String input, String rsaPublicKey) {
        RSA rsa = SecureUtil.rsa(null, rsaPublicKey);
        byte[] encrypt = rsa.encrypt(input, KeyType.PublicKey);
        return Base64Tool.base64Encode(encrypt);
    }

    public static String decryptByPrivateKey(String input, String rsaPrivateKey) {
        RSA rsa = SecureUtil.rsa(rsaPrivateKey, null);
        byte[] decrypt = rsa.decrypt(input, KeyType.PrivateKey);
        return new String(decrypt);
    }


    public PublicKey convertToPublicKey(String publicKeyStr) {
        if (publicKeyStr == null || publicKeyStr.isEmpty()) {
            return null;
        }
        try {
            // Base64解码
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);

            // 创建KeySpec并生成公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new RuntimeException(e);
        }
    }

//    public static PrivateKey convertToPrivateKey(String privateKeyStr) {
//        if (privateKeyStr == null || privateKeyStr.isEmpty()) {
//            return null;
//        }
//        try {
//            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
//            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            return keyFactory.generatePrivate(keySpec);
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static PrivateKey convertToPrivateKey(String privateKeyStr) {
        if (privateKeyStr == null || privateKeyStr.isEmpty()) {
            return null;
        }
        try {
            // 读取PEM文件内容
            // 清理PEM标记和空白
            String privateKeyPem = privateKeyStr
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
            // Base64解码
            byte[] decodedKey = Base64.getDecoder().decode(privateKeyPem);
            // 生成私钥对象
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

}
