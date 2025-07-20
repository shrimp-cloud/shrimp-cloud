package com.wkclz.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Created: wangkaicun @ 2018-03-18 下午1:50
 */
public class StringUtil {

    private static final char UNDERLINE = '_';

    private static final Pattern PATTERN = Pattern.compile("[\t\r\n]");

    /**
     * 下划线 转Camel
     *
     * @param param
     * @return
     */
    public static String underlineToCamel(String param) {
        if (param == null || param.trim().isEmpty()) {
            return param;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(param.charAt(i)));
                }
            } else {
                sb.append(c);
            }
            i++;
        }
        return sb.toString();
    }


    /**
     * 首字母转大写
     *
     * @param param
     * @return
     */
    public static String firstChatToUpperCase(String param) {
        if (param == null || param.trim().isEmpty()) {
            return param;
        }
        String fistChar = param.substring(0, 1).toUpperCase();
        return fistChar + param.substring(1);
    }


    /**
     * 驼峰转下划线
     *
     * @param param
     * @return
     */
    public static String camelToUnderline(String param) {
        if (param == null || param.trim().isEmpty()) {
            return param;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) {
                    sb.append(UNDERLINE);
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    /**
     * 字符串转换为Map
     *
     * @param str
     * @return
     */
    public static Map<String, String> strVar2Map(String str, String separator) {

        Map<String, String> varMap = new HashMap<>();
        if (str == null || "".equalsIgnoreCase(str)) {
            return varMap;
        }
        String[] variablesArr = str.split(separator);
        for (String varStr : variablesArr) {
            if (varStr.contains("=")) {
                String key = varStr.substring(0, varStr.indexOf("="));
                String value = varStr.substring(varStr.indexOf("=") + 1);
                varMap.put(key, value);
            }
        }
        return varMap;
    }


    /**
     * 把字符串能指定的内容替换成小写
     *
     * @param str
     * @param toLower
     * @return
     */
    public static String check2LowerCase(String str, String toLower) {
        if (str == null || toLower == null || str.trim().isEmpty() || toLower.trim().isEmpty()) {
            return str;
        }
        int index = str.indexOf(toLower);
        if (index < 0) {
            return str;
        }
        String lower = str.substring(index, index + toLower.length());
        str = str.substring(0, index) + lower.toLowerCase() + str.substring(index + toLower.length());
        return check2LowerCase(str, toLower);
    }

    /**
     * 移除特殊字符
     *
     * @param str
     * @return
     */
    public static String removeSpecialCharacters(String str) {
        String dest = "";
        if (str != null) {
            Matcher m = PATTERN.matcher(str);
            dest = m.replaceAll(" ");
            dest = dest.replaceAll("\\s+", " ");
        }
        return dest;
    }


    public static void main(String[] args) {
        String xx = "p.id desc, a.merchantId ASC,          r.merchantId DESC, q.customerId DESC";
        xx = check2LowerCase(xx, "ASC");
        xx = check2LowerCase(xx, "DESC");
        System.out.println(xx);
    }

}
