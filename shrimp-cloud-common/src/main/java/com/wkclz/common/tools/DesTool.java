package com.wkclz.common.tools;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class DesTool {

    public static final int KEY_56 = 56;


    /**
     * String 2 DES
     */
    public static String encrypt(String plainText, String seed) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = initCipher(seed, Cipher.ENCRYPT_MODE);
        try {
            byte[] result = cipher.doFinal(bytes);
            return Base64Tool.base64Encode(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES 2 String
     */

    public static String decrypt(String encryptText, String seed) {
        if (encryptText == null || encryptText.isEmpty()) {
            return encryptText;
        }
        Cipher cipher = initCipher(seed, Cipher.DECRYPT_MODE);
        byte[] bytes = Base64Tool.base64Decode(encryptText);
        try {
            byte[] result = cipher.doFinal(bytes);
            return new String(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher initCipher(String key, int cipherMode) {
        if (StringUtils.isBlank(key)) {
            throw  new RuntimeException("key is null or empty");
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("DES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes(StandardCharsets.UTF_8));
            kgen.init(KEY_56, secureRandom);
            Cipher cipher = Cipher.getInstance("DES");
            SecretKeySpec keySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), "DES");
            cipher.init(cipherMode, keySpec);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        String plaintext = "admin";
        String key = "keykeykeykey";
        System.out.println("plaintext：" + plaintext);
        System.out.println("key：" + key);

        String encrypt = encrypt(plaintext, key);
        System.out.println("encrypt：" + encrypt);
        String decrypt = decrypt(encrypt, key);
        System.out.println("decrypt：" + decrypt);
    }


}
