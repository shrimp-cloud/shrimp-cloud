package com.wkclz.common.tools;

import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class AesTool {

    public static final int KEY_128 = 128;
    public static final int KEY_192 = 192;
    public static final int KEY_256 = 256;


    /**
     * String 2 AES
     */
    public static String encrypt(String plainText, String seed) {
        return encrypt(plainText, seed, KEY_128);
    }
    public static String encrypt(String plainText, String seed, int keySize) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
        Cipher cipher = initCipher(seed, keySize, Cipher.ENCRYPT_MODE);
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
        return decrypt(encryptText, seed, KEY_128);
    }
    public static String decrypt(String encryptText, String seed, int keySize) {
        if (encryptText == null || encryptText.isEmpty()) {
            return encryptText;
        }
        Cipher cipher = initCipher(seed, keySize, Cipher.DECRYPT_MODE);
        byte[] bytes = Base64Tool.base64Decode(encryptText);
        try {
            byte[] result = cipher.doFinal(bytes);
            return new String(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher initCipher(String key, int keySize, int cipherMode) {
        if (StringUtils.isBlank(key)) {
            throw  new RuntimeException("key is null or empty");
        }
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes(StandardCharsets.UTF_8));
            kgen.init(keySize, secureRandom);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(kgen.generateKey().getEncoded(), "AES");
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

        String encrypt = encrypt(plaintext, key, KEY_256);
        System.out.println("encrypt：" + encrypt);
        String decrypt = decrypt(encrypt, key, KEY_256);
        System.out.println("decrypt：" + decrypt);
    }


}
