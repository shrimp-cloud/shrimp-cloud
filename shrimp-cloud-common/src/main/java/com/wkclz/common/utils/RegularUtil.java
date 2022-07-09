package com.wkclz.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 上午1:06
 */
public class RegularUtil {

    private static final Pattern IS_POSITIVE_INTEGER = Pattern.compile("^[1-9]\\d*$");
    private static final Pattern IS_LETTER = Pattern.compile("^[A-Za-z]+$");
    private static final Pattern IS_LEGAL_CHAR = Pattern.compile("^[0-9a-zA-Z_]{1,}$");
    private static final Pattern IS_DATE = Pattern.compile("((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)([-\\/\\._])(29)$))");
    private static final Pattern IS_EMAIL = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static final Pattern IS_MOBILE = Pattern.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");
    private static final Pattern IS_IP = Pattern.compile("(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))");
    private static final Pattern IS_DOMAIN = Pattern.compile("^(?=^.{3,255}$)(http(s)?:\\/\\/)?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(\\/\\w+\\.\\w+)*$");

    /**
     * 匹配正整数
     *
     * @param str
     * @return
     */
    public static boolean isPositiveInteger(String str) {
        return reg(str, IS_POSITIVE_INTEGER);
    }


    /**
     * 匹配是否为字母
     * @param str
     * @return
     */
    public static boolean isLetter(String str){
        return reg(str, IS_LETTER);
    }

    /**
     * 匹配是否为合法字符
     * @param str
     * @return
     */
    public static boolean isLegalChar(String str){
        return reg(str, IS_LEGAL_CHAR);
    }


    /**
     * 判断字符串是否为 yyyy-MM-dd HH:mm:ss 的时间格式【特殊日期识别】
     *
     * @param dateStr
     * @return
     */
    public static boolean isDate(String dateStr) {
        return reg(dateStr, IS_DATE);
    }

    /**
     * 判断字符串是否为邮箱
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        return reg(str, IS_EMAIL);
    }

    /**
     * 判断字符串是否为手机号
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        return reg(str, IS_MOBILE);
    }

    /**
     * 判断字符串是否为IP
     *
     * @param str
     * @return
     */
    public static boolean isDomain(String str) {
        return reg(str, IS_DOMAIN);
    }

    /**
     * 判断字符串是否为IP
     *
     * @param str
     * @return
     */
    public static boolean isIp(String str) {
        return reg(str, IS_IP);
    }



    /**
     * 判断字符串是否含有双字节字符
     *
     * @param str
     * @return
     */
    public static boolean haveDoubleByte(String str) {
        for (char c : str.toCharArray()) {
            if (isDoubleByte(c)) {
                // 有一个中文字符就返回
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符是否为双字节字符
     *
     * @param c
     * @return
     */
    public static boolean isDoubleByte(char c) {
        if (c >= 0x4E00 && c <= 0x9FA5) {
            // 有一个中文字符就返回
            return true;
        }
        return false;
    }


    /**
     * 正则判断过程
     *
     * @param str
     * @param pattern
     * @return
     */
    private static boolean reg(String str, Pattern pattern) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return pattern.matcher(str.trim()).matches();
    }


    public static void main(String[] args) {
        /*
        System.out.println(isIp("127.0.0.1"));
        System.out.println(isDomain("www.www.wklz.com"));
        System.out.println(isDate("2019-02-29"));
        System.out.println(isLetter("dDD"));
        */
    }

}
