package com.wkclz.mybatis.helper;

import cn.hutool.core.map.MapUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.utils.SecretUtil;
import com.wkclz.mybatis.base.PageData;
import com.wkclz.mybatis.base.PageHandle;
import com.wkclz.spring.config.SpringContextHolder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
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

    /**
     * 自定义 sql 查询-List
     * @param sql
     * @param param
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> List<T> selectList(String sql, T param){
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);
        return sqlSession.selectList(statement, param);
    }

    /**
     * 自定义 sql查询-page
     * @param sql
     * @param param
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> PageData<T> selectPage(String sql, T param){
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);
        PageHandle<T> pageHandle = new PageHandle<>(param);
        List<T> list = sqlSession.selectList(statement, param);
        PageData page = pageHandle.page(list);
        return page;
    }

    /**
     * 自定义 sql查询-List
     * @param sql
     * @param param
     * @return
     */
    public static List<Map> selectList(String sql, Map param){
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);
        List<Map> list = sqlSession.selectList(statement, param);
        list = (List<Map>)list.stream().map(MapUtil::toCamelCaseMap).collect(Collectors.toList());
        return list;
    }

    /**
     * 自定义 sql查询-page
     * @param sql
     * @param param
     * @return
     */
    public static PageData<Map> selectPage(String sql, Map param){
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        String statement = reloadSql(sql);

        Object pageNoObj = param.get("pageNo");
        Object pageSizeObj = param.get("pageSize");
        Integer pageNo = (pageNoObj == null)?0:Integer.parseInt(pageNoObj.toString());
        Integer pageSize = (pageSizeObj == null)?10:Integer.parseInt(pageSizeObj.toString());

        PageHelper.startPage(pageNo, pageSize);
        List<Map> list = sqlSession.selectList(statement, param);

        Page listPage = (Page) list;
        long total = listPage.getTotal();
        PageData<Map> pageData = new PageData<>(pageNo, pageSize);
        pageData.setTotalCount(Long.valueOf(total).intValue());
        list = (List<Map>)list.stream().map(MapUtil::toCamelCaseMap).collect(Collectors.toList());
        pageData.setRows(list);
        return pageData;
    }

    /**
     * 重新加载资源
     */
    private synchronized static String reloadSql(String sql) {
        String md5 = SecretUtil.md5(sql);
        String namespace = "namespace" + md5;
        String selectId = "select" + md5;
        String statement = namespace + "." + selectId;

        if (STATEMENTS.contains(statement)){
            return statement;
        }
        STATEMENTS.add(statement);

        SqlSession sqlSession = SpringContextHolder.getBean(SqlSession.class);
        Configuration configuration = sqlSession.getConfiguration();


        String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n"
            + "<mapper namespace=\""+ namespace +"\">\n"
            + "<select id=\""+selectId+"\" parameterType=\"java.util.Map\" resultType=\"java.util.Map\">\n"
            + sql
            + "\n</select>\n</mapper>";

        Object o = System.getProperties().get("user.dir");
        String userDir = o.toString();
        String savePath = userDir + "/temp/mapper/" ;
        String filePath = savePath + namespace + ".xml";

        FileWriter writer;
        try {

            //文件保存位置
            File saveDir = new File(savePath);
            if(!saveDir.exists()){
                saveDir.mkdirs();
            }

            File file = new File(filePath);
            if (!file.exists()){
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

    /**
     *  删除xml元素的节点缓存
     *  @param nameSpace xml中命名空间
     */
    private static void clearMap(Configuration configuration, String nameSpace) {
        logger.info("清理Mybatis的namespace={}在mappedStatements、caches、resultMaps、parameterMaps、keyGenerators、sqlFragments中的缓存", nameSpace);
        Arrays.asList("mappedStatements", "caches", "resultMaps", "parameterMaps", "keyGenerators", "sqlFragments").forEach(fieldName -> {
            Object value = getFieldValue(configuration, fieldName);
            if (value instanceof Map) {
                Map<?, ?> map = (Map)value;
                List<Object> list = map.keySet().stream().filter(o -> o.toString().startsWith(nameSpace + ".")).collect(Collectors.toList());
                logger.info("需要清理的元素: {}", list);
                list.forEach(k -> map.remove((Object)k));
            }
        });
    }

    /**
     *  清除文件记录缓存
     */
    private static void clearSet(Configuration configuration, String resource) {
        logger.debug("清理mybatis的资源{}在容器中的缓存", resource);
        Object value = getFieldValue(configuration, "loadedResources");
        if (value instanceof Set) {
            Set<?> set = (Set)value;
            set.remove(resource);
            set.remove("namespace:" + resource);
        }
    }

    /**
     *  获取对象指定属性
     */
    private static Object getFieldValue(Object obj, String fieldName){
        logger.debug("从{}中加载{}属性", obj, fieldName);
        try{
            Field field = obj.getClass().getDeclaredField(fieldName);
            if (!field.canAccess(obj)) {
                field.setAccessible(true);
            }
            return field.get(obj);
        }catch(Exception e){
            logger.debug("ERROR: 加载对象中[{}]", fieldName, e);
            throw new RuntimeException("ERROR: 加载对象失败[" + fieldName + "]", e);
        }
    }

    /**
     *  获取xml的namespace
     */
    private static String getNamespace(Resource resource){
        logger.debug("从{}获取namespace", resource.toString());
        try{
            XPathParser parser = new XPathParser(resource.getInputStream(), true, null, new XMLMapperEntityResolver());
            return parser.evalNode("/mapper").getStringAttribute("namespace");
        }catch(Exception e){
            logger.debug("ERROR: 解析xml中namespace失败", e);
            throw new RuntimeException("ERROR: 解析xml中namespace失败", e);
        }
    }

}
