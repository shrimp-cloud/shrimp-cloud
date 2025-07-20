package com.wkclz.common.utils;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    private static final Pattern PATTERN = Pattern.compile("\\s*");


    /**
     * 读取 json 文件
     *
     * @return
     */
    public static <T> T readJson(String jsonFilePath, Class<T> clazz) {
        if (StringUtils.isBlank(jsonFilePath)) {
            return null;
        }
        String str = FileUtil.readFile(jsonFilePath);
        str = replaceBlank(str);
        return JSON.parseObject(str, clazz);
    }

    /**
     * 把 Object 写入到文件
     *
     * @param object
     */
    public static void writeJson(String jsonFilePath, Object object) {
        FileWriter writer = null;
        try {
            File file = new File(jsonFilePath);
            if (!file.isFile()) {
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    logger.info("file exist: {}", file.getAbsolutePath());
                }
            }
            String jsonStr = JSON.toJSONString(object);
            jsonStr = format(jsonStr);

            writer = new FileWriter(file);
            writer.write(jsonStr);
            writer.flush();
        } catch (IOException e) {
            // who care ?
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }

    }

    /**
     * json 字符串格式化，增强可读性
     *
     * @param s
     * @return
     */
    private static String format(String s) {

        int level = 0;
        //存放格式化的json字符串
        StringBuffer jsonForMatStr = new StringBuffer();

        //将字符串中的字符逐个按行输出
        char quotationMark = '"';
        Integer quotationMarks = 0;
        for (int index = 0; index < s.length(); index++) {
            // 获取s中的每个字符
            char c = s.charAt(index);
            if (quotationMark == c) {
                quotationMarks++;

            }

            //level大于0并且jsonForMatStr中的最后一个字符为\n,jsonForMatStr加入\t
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            //遇到"{"和"["要增加空格和换行，遇到"}"和"]"要减少空格，以对应，遇到","要换行
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c);
                    if (quotationMarks % 2 == 0) {
                        jsonForMatStr.append("\n");
                    }
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();

    }

    /**
     * 辅助json 格式化
     *
     * @param level
     * @return
     */
    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int leveli = 0; leveli < level; leveli++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }


    /**
     * 移除字符串内的非正常字符
     * Pattern.compile("\\s*|\t|\r|\n")
     *
     * @param str
     * @return
     */
    private static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Matcher m = PATTERN.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
