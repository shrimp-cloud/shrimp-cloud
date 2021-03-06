package com.wkclz.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Properties 相关处理类
 *
 * @author wangkc
 * @mail admin@wkclz.com
 * @since 2017-01-15 13:55:02
 */
public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * Properties 转换为 Map
     *
     * @param properties
     * @author wangkc
     * @mail admin@wkclz.com
     * @since 2017-01-15 13:56:24
     */
    public static Map<String, Object> prop2Map(Properties properties) {
        Map<String, Object> map = new HashMap<>();
        Set<Object> keySet = properties.keySet();
        for (Object key : keySet) {
            map.put(key.toString(), properties.get(key));
        }
        return map;
    }

    // 文件转 Properties
    public static Properties propFile2Prop(String fileStr) {

        InputStream in = null;
        Properties prop = new Properties();
        try {
            in = new BufferedInputStream(new FileInputStream(new File(fileStr)));
            prop.load(in);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return prop;
    }

    /**
     * @param @param  file
     * @param @return
     * @param @throws IOException    设定文件
     * @throws
     * @Title:
     * @Description:
     * @author wangkc admin@wkclz.com
     * @date 2017年5月31日 下午1:35:19 *
     */
    public static Map<String, Object> propFile2Map(String file) {
        Properties prop = propFile2Prop(file);
        return prop2Map(prop);
    }


    // 从文件读取 Properties
    public static Properties readProp(String propertiesPath) {
        File file = new File(propertiesPath);
        if (!file.exists()) {
            return null;
        }

        InputStream in = null;
        Properties props = new Properties();
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            props.load(in);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return props;

    }

    // 向文件写入 Properties
    public static void writeProp(String propertiesPath, Properties newProps) {
        Properties oldProp = readProp(propertiesPath);
        newProps.forEach((propKey, propValue) -> {
            oldProp.setProperty(propKey.toString(), propValue.toString());
        });

        Properties sortProp = MapUtil.map2Prop(MapUtil.sortMapByKey(MapUtil.prop2Map(oldProp)));

        File file = new File(propertiesPath);
        if (!file.exists()) {
            file.mkdirs();
            try {
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    logger.info("file exist: {}", file.getAbsolutePath());
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        Writer fw = null;
        try {
            fw = new FileWriter(propertiesPath);
            sortProp.store(fw, "此属性文件由程序自动管理，请不要手动编辑");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Properties 2 Object
     *
     * @param prop
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object prop2Object(Properties prop, Class<?> clazz) {
        if (prop == null) {
            return null;
        }
        Object obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, prop.get(field.getName()));
            }
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return obj;
    }


}
