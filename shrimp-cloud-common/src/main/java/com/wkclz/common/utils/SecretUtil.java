package com.wkclz.common.utils;

import com.wkclz.common.annotation.Desc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-23 上午12:28
 */
public class SecretUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecretUtil.class);

    // 使用时请覆盖此 salt
    private static final String GENERAL_SALT = "shrimp@450330#cc$wkclz";


    // 32个字符，用来表示32进制
    private final static char[] DIGITS = {
        '0' , '1' , '2' , '3' , '4' , '5' ,
        '6' , '7' , '8' , '9' , 'a' , 'b' ,
        'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
        'j' , 'k' , 'l' , 'm' , 'n' , 'p' ,
        'q' , 'r' , 't' , 'u' , 'v' , 'w' ,
        'x' , 'y'
    };



    /**
     * MORE
     */

    /**
     * 获取 6 位随机数
     *
     * @return
     */
    public static String getCapchaCode() {
        return (int) ((Math.random() * 9 + 1) * 100000) + "";
    }


    /**
     * 密码加密处理
     *
     * @param pwdStr
     * @param salt
     * @return
     */
    public static String getEncryptPassword(String pwdStr, String salt) {
        String encryptPassword = null;
        try {
            encryptPassword = aesEncrypt(pwdStr, salt);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return encryptPassword;
    }

    @Deprecated
    @Desc("请使用 getEncryptPassword(pwdStr, GENERAL_SALT)，自定义 salt")
    public static String getEncryptPassword(String pwdStr) {
        return getEncryptPassword(pwdStr, GENERAL_SALT);
    }

    /**
     * 密码解密处理
     *
     * @param encryptPwdStr
     * @param salt
     * @return
     */
    public static String getDecryptPassword(String encryptPwdStr, String salt) {
        String decryptPassword = null;
        try {
            decryptPassword = aesDecrypt(encryptPwdStr, salt);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return decryptPassword;
    }

    public static String getDecryptPassword(String encryptPwdStr) {
        return getDecryptPassword(encryptPwdStr, GENERAL_SALT);
    }


    /**
     * BASE
     */


    /**
     * getKey for AES
     *
     * @return
     */
    public static String getKey() {
        String uuid = UUID.randomUUID().toString();
        return md5(uuid);
    }

    /**
     * String 2 MD5
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
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


    /**
     * byte[] 2 Base64
     *
     * @param b
     * @return
     */
    public static String base64Encode(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }
    public static String base64Encode(String b) {
        if (b == null){
            return null;
        }
        return Base64.getEncoder().encodeToString(b.getBytes());
    }

    /**
     * Base64 2 byte[]
     *
     * @param base64Code
     * @return
     * @throws Exception
     */
    public static byte[] base64Decode(String base64Code)  {
        return Base64.getDecoder().decode(base64Code);
    }
    public static String base64Decode2String(String base64Code) {
        byte[] decode = Base64.getDecoder().decode(base64Code);
        return new String(decode);
    }

    /**
     * String 2 AES
     *
     * @param content
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(encryptKey.getBytes("UTF-8"));
        kgen.init(128, secureRandom);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] result = cipher.doFinal(content.getBytes("utf-8"));
        return base64Encode(result);
    }

    /**
     * AES 2 String
     *
     * @param encryptContent
     * @param decryptKey
     * @return
     * @throws Exception
     */
    public static String aesDecrypt(String encryptContent, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(decryptKey.getBytes("UTF-8"));
        kgen.init(128, secureRandom);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
        byte[] result = cipher.doFinal(base64Decode(encryptContent));
        return new String(result);
    }


    /**
     * Test
     * @param args
     * @throws Exception
     */
    /*
    public static void main(String[] args) throws Exception {
        String xx = "admin";
        System.out.println("xx：" + xx);
        String key = getKey();
        System.out.println("key：" + key);
        String aesEncrypt = aesEncrypt(xx, key);
        System.out.println("aesEncrypt：" + aesEncrypt);
        String aesDecrypt = aesDecrypt(aesEncrypt, key);
        System.out.println("aesDecrypt：" + aesDecrypt);
    }
    */


    /**
     * 密码简单对称加密示例
     *
     * @param args
     */
    public static void main(String[] args) {
        String encryptPassword = SecretUtil.getEncryptPassword("your password");
        System.out.println(encryptPassword);
    }


    /**
     * 数字转32进制
     * @param val
     * @return
     */
    public static String digits32(long val) {
        // 32=2^5=二进制100000
        int shift = 5;
        int mag = Long.SIZE - Long.numberOfLeadingZeros(val);
        int len = Math.max(((mag + (shift - 1)) / shift), 1);
        char[] buf = new char[len];
        do {
            // &31相当于%32
            buf[--len] = DIGITS[((int) val) & 31];
            val >>>= shift;
        } while (val != 0 && len > 0);
        return new String(buf);
    }


}
