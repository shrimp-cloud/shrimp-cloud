package com.wkclz.mybatis.helper;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.tools.Md5Tool;
import com.wkclz.mybatis.base.PageData;
import com.wkclz.spring.config.SpringContextHolder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class MyBatisHelper {

    private final static Logger logger = LoggerFactory.getLogger(MyBatisHelper.class);
    private static final Set<String> STATEMENTS = new HashSet<>();

    private final static String LT_TAG = "[__l_t__]";
    private final static String GT_TAG = "[__g_t__]";
    private final static List<String> XML_TAG = Arrays.asList(
        "if",
        "set",
        "trim",
        "when",
        "bind",
        "where",
        "choose",
        "foreach",
        "otherwise"
        );


    /**
     * 自定义 sql 查询-List
     *
     * @param sql
     * @param param
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> List<T> selectList(String sql, T param) {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);
        return sqlSession.selectList(statement, param);
    }

    /**
     * 自定义 sql查询-page
     *
     * @param sql
     * @param param
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> PageData<T> selectPage(String sql, T param) {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);
        param.init();
        try {
            PageHelper.startPage(param.getCurrent().intValue(), param.getSize().intValue());
            List<T> list = sqlSession.selectList(statement, param);
            Page listPage = (Page) list;
            long total = listPage.getTotal();
            PageData<T> pageData = new PageData<>(param);
            pageData.setTotal(total);
            pageData.setRows(list);
            return pageData;
        } finally {
            PageHelper.clearPage();
        }
    }

    /**
     * 自定义 sql查询-List
     *
     * @param sql
     * @param param
     * @return
     */
    public static List<LinkedHashMap> selectList(String sql, Map param) {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);
        List<LinkedHashMap> list = sqlSession.selectList(statement, param);
        list = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return list;
    }

    /**
     * 自定义 sql查询-page
     *
     * @param sql
     * @param param
     * @return
     */
    public static PageData<LinkedHashMap> selectPage(String sql, Map param) {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);

        Object currentObj = param.get("current");
        Object sizeObj = param.get("size");
        Long current = (currentObj == null) ? 0L : Long.parseLong(currentObj.toString());
        Long size = (sizeObj == null) ? 10L : Long.parseLong(sizeObj.toString());
        try {
            PageHelper.startPage(current.intValue(), size.intValue());
            List list = sqlSession.selectList(statement, param);
            Page listPage = (Page) list;
            long total = listPage.getTotal();
            PageData pageData = new PageData<>(current, size);
            pageData.setTotal(total);
            List rows = list.stream().toList();
            pageData.setRows(rows);
            return pageData;
        } finally {
            PageHelper.clearPage();
        }
    }

    /**
     * 重新加载资源
     */
    public synchronized static String reloadSql(String sql) {
        String md5 = Md5Tool.md5(sql);
        String namespace = "namespace_" + md5;
        String selectId = "select_" + md5;
        String statement = namespace + "." + selectId;

        if (STATEMENTS.contains(statement)) {
            return statement;
        }
        STATEMENTS.add(statement);

        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        Configuration configuration = sqlSession.getConfiguration();

        /**
         * xml 和 sql 标签冲突处理
         */
        // 将所有属于 xml 的 < 和 > ，通过使用自定义字符串的方式保留下来
        for (String tag : XML_TAG) {
            // 只找左边 <, 将自动处理 右边 > 【考虑大小写，起始，结束标签 2x2=4个场景】
            sql = findAndreplaceXmlTag(sql, "<" + tag);
            sql = findAndreplaceXmlTag(sql, "</" + tag);
            sql = findAndreplaceXmlTag(sql, "<" + tag.toUpperCase());
            sql = findAndreplaceXmlTag(sql, "</" + tag.toUpperCase());
        }
        // 将所有属于 sql 的 < 和 >, 使用转译语法处理
        sql = sql.replace("<", "&lt;");
        sql = sql.replace(">", "&gt;");
        // 将所有属于xml 的 < 和 > 还原
        sql = sql.replace(LT_TAG, "<");
        sql = sql.replace(GT_TAG, ">");

        String xmlStr = getXmlStr(sql, namespace, selectId);

        Object o = System.getProperties().get("user.dir");
        String userDir = o.toString();
        String savePath = userDir + "/temp/mapper/";
        String filePath = savePath + namespace + ".xml";

        FileWriter writer;
        try {

            //文件保存位置
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            writer = new FileWriter(file);
            writer.write("");
            writer.write(xmlStr);
            writer.flush();
            writer.close();

            clearMap(configuration, namespace);
            clearSet(configuration, filePath);

            InputStream inputStream = new FileInputStream(filePath);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(inputStream, configuration, filePath, configuration.getSqlFragments());
            xmlMapperBuilder.parse();

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return statement;
        }
        return statement;
    }

    @NotNull
    private static String getXmlStr(String sql, String namespace, String selectId) {
        String xmlStr = """
            <?xml version="1.0" encoding="UTF-8"?>
            <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
            <mapper namespace="{{namespace}}">
                <select id="{{selectId}}" parameterType="java.util.Map" resultType="java.util.LinkedHashMap">
                    {{sql}}
                </select>
            </mapper>
            """;

        xmlStr = xmlStr.replace("{{namespace}}", namespace);
        xmlStr = xmlStr.replace("{{selectId}}", selectId);
        xmlStr = xmlStr.replace("{{sql}}", sql);
        return xmlStr;
    }

    /**
     * 删除xml元素的节点缓存
     *
     * @param nameSpace xml中命名空间
     */
    private static void clearMap(Configuration configuration, String nameSpace) {
        logger.debug("清理Mybatis的namespace={}在mappedStatements、caches、resultMaps、parameterMaps、keyGenerators、sqlFragments中的缓存", nameSpace);
        Arrays.asList("mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments").forEach(fieldName -> {
            Object value = getFieldValue(configuration, fieldName);
            if (value instanceof Map) {
                Map<?, ?> map = (Map) value;
                List<Object> list = map.keySet().stream().filter(o -> o.toString().startsWith(nameSpace + ".")).collect(Collectors.toList());
                logger.debug("需要清理的元素: {}", list);
                list.forEach(k -> map.remove((Object) k));
            }
        });
    }

    /**
     * 清除文件记录缓存
     */
    private static void clearSet(Configuration configuration, String resource) {
        logger.debug("清理mybatis的资源{}在容器中的缓存", resource);
        Object value = getFieldValue(configuration, "loadedResources");
        if (value instanceof Set) {
            Set<?> set = (Set) value;
            set.remove(resource);
            set.remove("namespace:" + resource);
        }
    }

    /**
     * 获取对象指定属性
     */
    private static Object getFieldValue(Object obj, String fieldName) {
        logger.debug("从{}中加载{}属性", obj, fieldName);
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            if (!field.canAccess(obj)) {
                field.setAccessible(true);
            }
            return field.get(obj);
        } catch (Exception e) {
            logger.debug("ERROR: 加载对象中[{}]", fieldName, e);
            throw new RuntimeException("ERROR: 加载对象失败[" + fieldName + "]", e);
        }
    }

    /**
     * 获取xml的namespace
     */
    private static String getNamespace(Resource resource) {
        logger.debug("从{}获取namespace", resource.toString());
        try {
            XPathParser parser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
            return parser.evalNode("/mapper").getStringAttribute("namespace");
        } catch (Exception e) {
            logger.debug("ERROR: 解析xml中namespace失败", e);
            throw new RuntimeException("ERROR: 解析xml中namespace失败", e);
        }
    }

    /**
     * 使用特殊符号替代 < 和 >
     * 只需要传入带 < 的，将会自动查找和处理 >
     */
    private static String findAndreplaceXmlTag(String sql, String tag) {
        for (;;) {
            int ltIdx = sql.indexOf(tag);
            if (ltIdx == -1) {
                break;
            }
            // 替换 <
            sql = sql.substring(0, ltIdx) + LT_TAG + sql.substring(ltIdx + 1);
            // 替换 >
            ltIdx = ltIdx + 8;
            int gtIdx = sql.indexOf(">", ltIdx);
            if (gtIdx > -1) {
                sql = sql.substring(0, gtIdx) + GT_TAG + sql.substring(gtIdx + 1);
            }
        }
        return sql;
    }

}
