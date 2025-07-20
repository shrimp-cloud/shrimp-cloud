package com.wkclz.common.utils;

import com.wkclz.common.annotation.Desc;
import com.wkclz.common.tools.AesTool;
import com.wkclz.common.tools.Md5Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final char[] DIGITS = {
        '0' , '1' , '2' , '3' , '4' , '5' ,
        '6' , '7' , '8' , '9' , 'a' , 'b' ,
        'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
        'j' , 'k' , 'm' , 'n' , 'p' , 'q' ,
        'r' , 's' , 't' , 'u' , 'v' , 'w' ,
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
            encryptPassword = AesTool.encrypt(pwdStr, salt);
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
            decryptPassword = AesTool.decrypt(encryptPwdStr, salt);
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


    public static String getJavaUuid() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        return s.replace("-", "").toLowerCase();
    }

    /**
     * getKey for AES
     *
     * @return
     */
    public static String getKey() {
        String javaUuid = getJavaUuid();
        long l = System.currentTimeMillis();
        return Md5Tool.md5(javaUuid + l);
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



    /**
     * 密码简单对称加密示例
     *
     * @param args
     */
    public static void main(String[] args) {
        String encryptPassword = SecretUtil.getEncryptPassword("your password");
        System.out.println(encryptPassword);
    }

}
