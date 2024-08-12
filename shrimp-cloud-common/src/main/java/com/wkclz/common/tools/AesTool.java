package com.wkclz.common.tools;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AesTool {


    /**
     * String 2 AES
     */
    public static String encrypt(String plainText, String encryptKey) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        if (StringUtils.isBlank(encryptKey)) {
            throw  new RuntimeException("encryptKey is null or empty");
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(encryptKey.getBytes(StandardCharsets.UTF_8));
            kgen.init(128, secureRandom);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
            byte[] result = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64Tool.base64Encode(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES 2 String
     */
    public static String decrypt(String encryptText, String decryptKey) {
        if (encryptText == null || encryptText.isEmpty()) {
            return encryptText;
        }
        if (StringUtils.isBlank(decryptKey)) {
            throw  new RuntimeException("decryptKey is null or empty");
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(decryptKey.getBytes(StandardCharsets.UTF_8));
            kgen.init(128, secureRandom);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
            byte[] result = cipher.doFinal(Base64Tool.base64Decode(encryptText));
            return new String(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
