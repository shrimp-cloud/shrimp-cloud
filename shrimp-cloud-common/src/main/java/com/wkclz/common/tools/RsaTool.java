package com.wkclz.common.tools;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.wkclz.common.exception.BizException;

import java.security.*;

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

}
