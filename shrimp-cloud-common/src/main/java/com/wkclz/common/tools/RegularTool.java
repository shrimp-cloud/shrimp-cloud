package com.wkclz.common.tools;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shrimp
 * Created: wangkaicun @ 2017-10-20 上午1:06
 */

public class RegularTool {

    private static final Map<String, Pattern> PATTERN_MAP = new HashMap<>();

    private static final Pattern IS_POSITIVE_INTEGER = Pattern.compile("^[1-9]\\d*$");
    private static final Pattern IS_LETTER = Pattern.compile("^[A-Za-z]+$");
    private static final Pattern IS_LEGAL_CHAR = Pattern.compile("^[0-9a-zA-Z_]{1,}$");
    private static final Pattern IS_DATE = Pattern.compile("((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-/._])(10|12|0?[13578])([-/._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-/._])(11|0?[469])([-/._])(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-/._])(0?2)([-/._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)([-/._])(0?2)([-/._])(29)$)|(^([3579][26]00)([-/._])(0?2)([-/._])(29)$)|(^([1][89][0][48])([-/._])(0?2)([-/._])(29)$)|(^([2-9][0-9][0][48])([-/._])(0?2)([-/._])(29)$)|(^([1][89][2468][048])([-/._])(0?2)([-/._])(29)$)|(^([2-9][0-9][2468][048])([-/._])(0?2)([-/._])(29)$)|(^([1][89][13579][26])([-/._])(0?2)([-/._])(29)$)|(^([2-9][0-9][13579][26])([-/._])(0?2)([-/._])(29)$))");
    private static final Pattern IS_EMAIL = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static final Pattern IS_MOBILE = Pattern.compile("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$");
    private static final Pattern IS_IP = Pattern.compile("(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))");
    private static final Pattern IS_DOMAIN = Pattern.compile("^(?=^.{3,255}$)(http(s)?://)?(www\\.)?[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+(:\\d+)*(/\\w+\\.\\w+)*$");
    private static final Pattern IS_URL = Pattern.compile("^(https?://)?(www\\.)?(([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|localhost|\\d{1,3}(\\.\\d{1,3}){3}|\\$$?[a-fA-F0-9:]+\\$$?)(:\\d+)?(/[\\-a-zA-Z0-9@:%._+~#&/=]*)?(\\?[;&a-zA-Z0-9%_.~+=-]*)?(#[-a-zA-Z0-9_]*)?$");

    /**
     * 匹配正整数
     */
    public static boolean isPositiveInteger(String str) {
        return match(str, IS_POSITIVE_INTEGER);
    }


    /**
     * 匹配是否为字母
     */
    public static boolean isLetter(String str){
        return match(str, IS_LETTER);
    }

    /**
     * 匹配是否为合法字符
     */
    public static boolean isLegalChar(String str){
        return match(str, IS_LEGAL_CHAR);
    }


    /**
     * 判断字符串是否为 yyyy-MM-dd HH:mm:ss 的时间格式【特殊日期识别】
     */
    public static boolean isDate(String dateStr) {
        return match(dateStr, IS_DATE);
    }

    /**
     * 判断字符串是否为邮箱
     */
    public static boolean isEmail(String str) {
        return match(str, IS_EMAIL);
    }

    /**
     * 判断字符串是否为手机号
     */
    public static boolean isMobile(String str) {
        return match(str, IS_MOBILE);
    }

    /**
     * 判断字符串是否为IP
     */
    public static boolean isDomain(String str) {
        return match(str, IS_DOMAIN);
    }

    /**
     * 判断字符串是否为IP
     */
    public static boolean isIp(String str) {
        return match(str, IS_IP);
    }

    /**
     * 判断字符串是否为IP
     */
    public static boolean isUrl(String str) {
        return match(str, IS_URL);
    }


    /**
     * 判断字符串是否含有双字节字符
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
     */
    public static boolean isDoubleByte(char c) {
        // 有一个中文字符就返回
        return c >= 0x4E00 && c <= 0x9FA5;
    }



    public static boolean match(String str, String regex) {
        if (str == null || regex == null) {
            return false;
        }
        Pattern pattern = getPattern(regex);
        return match(str, pattern);
    }

    public static boolean match(String str, Pattern pattern) {
        if (str == null || pattern == null) {
            return false;
        }
        return pattern.matcher(str).matches();
    }

    public static List<String> find(String str, String regex) {
        if (str == null || regex == null) {
            return Collections.emptyList();
        }
        Pattern pattern = getPattern(regex);

        // 创建matcher对象
        Matcher matcher = pattern.matcher(str);
        // 遍历所有匹配的结果
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            // 提取匹配的数字
            String rt = matcher.group();
            result.add(rt);
        }
        return result;

    }

    public static String replaceAll(String str, String regex, String replacement) {
        if (str == null) {
            return "";
        }
        if (regex == null) {
            return str;
        }
        if (replacement == null) {
            replacement = "";
        }
        return str.replaceAll(regex, replacement);
    }

    private static Pattern getPattern(String regex) {
        return PATTERN_MAP.computeIfAbsent(regex, Pattern::compile);
    }



    public static void main(String[] args) {
        System.out.println(isIp("127.0.0.1"));
        System.out.println(isDomain("www.www.wklz.com"));
        System.out.println(isDate("2019-02-29"));
        System.out.println(isLetter("dDD"));
        System.out.println(isUrl("https://www.wkclz.com/xxx/xxxx?aa=bb#ddd"));
    }


}
