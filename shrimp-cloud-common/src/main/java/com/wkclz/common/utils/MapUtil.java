package com.wkclz.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MapUtil {

    private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

    /**
     * Object 2 Map
     *
     * @param objs
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> obj2Map(T... objs) {
        Map<String, Object> map = new HashMap<>();
        for (T obj : objs) {
            Map m = new BeanMap(obj);
            map.putAll(m);
        }
        return map;

        /* 反射方式 obj2Map
        try {
            for (T obj : objs){
                // 考虑父类存在的情况
                Class superClass = obj.getClass().getSuperclass();
                if (superClass != null){
                    Field[] superClassFields = superClass.getDeclaredFields();
                    for(Field field : superClassFields){
                        field.setAccessible(true);
                        map.put(field.getName(), field.get(obj));
                    }
                }
                // it self
                Field[] fields = obj.getClass().getDeclaredFields();
                for(Field field : fields){
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }
            }
        } catch (IllegalAccessException e) {
            // who care ?
        }
        return map;
        */
    }

    /**
     * Object 2 Map to List
     *
     * @param objs
     * @return
     * @throws Exception
     */
    public static <T> List<Map<String, Object>> obj2MapList(T... objs) {
        List<Map<String, Object>> list = new ArrayList();
        for (T obj : objs) {
            Map<String, Object> map = obj2Map(obj);
            list.add(map);
        }
        return list;
    }


    /**
     * Maps 2 ObjectList
     *
     * @param maps
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> List<T> map2ObjList(List<Map> maps, Class<T> clazz) {
        if (maps == null || maps.size() == 0) {
            return null;
        }
        List<T> list = new ArrayList<>();
        maps.forEach(map -> {
            list.add(map2Obj(map, clazz));
        });
        return list;
    }


    /**
     * Map 2 Object
     *
     * @param map
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T map2Obj(Map map, Class<T> clazz) {
        if (map == null) {
            return null;
        }
        T obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.populate(obj, map);
        } catch (InstantiationException e) {
            logger.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.error(e.getMessage(), e);
        }
        return obj;
    }


    /**
     * jsonString 2 Map
     *
     * @param jsonString
     * @return
     */
    public static Map<String, Object> jsonString2Map(String jsonString) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(jsonString)) {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
            entries.forEach(entry -> map.put(entry.getKey(), entry.getValue()));
        }
        return map;
    }


    /**
     * 驼峰转换
     *
     * @param maps
     * @return
     */
    public static <T> List<Map<String, T>> toReplaceKeyLow(List<Map<String, T>> maps) {
        List<Map<String, T>> rts = new ArrayList<>();
        for (Map<String, T> map : maps) {
            rts.add(toReplaceKeyLow(map));
        }
        return rts;
    }

    /**
     * 驼峰转换
     *
     * @param map
     * @return
     */
    public static <T> Map<String, T> toReplaceKeyLow(Map<String, T> map) {
        Map reRap = new HashMap();
        if (reRap != null) {
            Iterator var2 = map.entrySet().iterator();
            while (var2.hasNext()) {
                Map.Entry<String, T> entry = (Map.Entry) var2.next();
                reRap.put(StringUtil.underlineToCamel(entry.getKey()), map.get(entry.getKey()));
            }
            map.clear();
        }
        return reRap;
    }


    /**
     * @param @param  map
     * @param @return 设定文件
     * @throws
     * @Title: removeBlank
     * @Description:
     * @author wangkc admin@wkclz.com
     * @date 2017年4月1日 上午12:05:10 *
     */
    public static <T> Map<String, T> removeBlank(Map<String, T> map) {
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (map.get(key) != null && map.get(key).toString().trim().length() == 0) {
                map.put(key, null);
            }
        }
        return map;
    }

    public static <T> String map2UrlString(Map<String, T> map) {
        StringBuffer str = new StringBuffer();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            str.append(key)
                .append("=")
                .append(map.get(key).toString())
                .append("&");
        }
        return str.substring(0, str.length() - 1);
    }


    /**
     * Properties 转 Map
     *
     * @param prop
     * @return
     */
    public static Map<String, String> prop2Map(Properties prop) {
        Map<String, String> map = new HashMap<>();
        prop.forEach((propKey, propValue) -> {
            map.put(propKey.toString(), propValue.toString());
        });
        return map;
    }

    /**
     * @param map 转 Properties
     * @return
     */
    public static Properties map2Prop(Map<String, String> map) {
        Properties prop = new Properties();
        Set<String> sets = map.keySet();
        sets.forEach(set -> {
            if (map.get(set) != null) {
                prop.setProperty(set, map.get(set));
            }
        });
        return prop;
    }

    /**
     * Map 排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {
            return str1.compareTo(str2);
        }
    }


}
