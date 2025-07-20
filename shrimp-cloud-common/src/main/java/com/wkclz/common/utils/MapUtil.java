package com.wkclz.common.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
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
    public static <T> LinkedHashMap<String, Object> obj2Map(T... objs) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        // 反射方式 obj2Map
        for (T obj : objs) {
            // 考虑父类存在的情况
            Class superClass = obj.getClass().getSuperclass();
            if (superClass != null) {
                Field[] superClassFields = superClass.getDeclaredFields();
                for (Field field : superClassFields) {
                    field.setAccessible(true);
                    String key = field.getName();
                    Object value = null;
                    try {
                        value = field.get(obj);
                    } catch (IllegalAccessException e) {
                        logger.error("object to map fail: {}", e.getMessage());
                    }
                    map.put(key, value);
                }
            }
            // it self
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String key = field.getName();
                Object value = null;
                try {
                    value = field.get(obj);
                } catch (IllegalAccessException e) {
                    logger.error("object to map fail: {}", e.getMessage());
                }
                map.put(key, value);
            }
        }
        return map;

    }

    /**
     * Object 2 Map to List
     *
     * @param objs
     * @return
     * @throws Exception
     */
    @SafeVarargs
    public static <T> List<LinkedHashMap<String, Object>> obj2MapList(T... objs) {
        List<LinkedHashMap<String, Object>> list = new ArrayList();
        for (T obj : objs) {
            LinkedHashMap<String, Object> map = obj2Map(obj);
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
    public static <M extends HashMap, T> List<T> map2ObjList(List<M> maps, Class<T> clazz) {
        if (maps == null || maps.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        maps.forEach(map -> list.add(map2Obj(map, clazz)));
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
            BeanUtils.copyProperties(map, obj);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
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
     */
    public static List<Map> toReplaceMapKeyLow(List<Map> maps) {
        List<Map> rts = new ArrayList<>();
        for (Map map : maps) {
            rts.add(toReplaceMapKeyLow(map));
        }
        return rts;
    }
    public static List<LinkedHashMap> toReplaceLinkedHashMapKeyLow(List<LinkedHashMap> maps) {
        List<LinkedHashMap> rts = new ArrayList<>();
        for (LinkedHashMap map : maps) {
            rts.add(toReplaceLinkedHashMapKeyLow(map));
        }
        return rts;
    }

    /**
     * 驼峰转换
     *
     * @param map
     * @return
     */
    public static Map toReplaceMapKeyLow(Map map) {
        Map reRap = new HashMap();
        if (map != null) {
            Iterator var2 = map.entrySet().iterator();
            while (var2.hasNext()) {
                Map.Entry entry = (Map.Entry) var2.next();
                String key = entry.getKey().toString();
                reRap.put(StringUtil.underlineToCamel(key), map.get(key));
            }
            map.clear();
        }
        return reRap;
    }
    public static LinkedHashMap toReplaceLinkedHashMapKeyLow(LinkedHashMap map) {
        LinkedHashMap reRap = new LinkedHashMap();
        if (map != null) {
            Iterator var2 = map.entrySet().iterator();
            while (var2.hasNext()) {
                Map.Entry entry = (Map.Entry) var2.next();
                String key = entry.getKey().toString();
                reRap.put(StringUtil.underlineToCamel(key), map.get(key));
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
            T t = map.get(key);
            if (t != null && t.toString().trim().isEmpty()) {
                map.put(key, null);
            }
        }
        return map;
    }

    public static <T> String map2UrlString(Map<String, T> map) {
        StringBuilder str = new StringBuilder();
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
        prop.forEach((propKey, propValue) -> map.put(propKey.toString(), propValue.toString()));
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
            return Collections.emptyMap();
        }
        Map<String, String> sortMap = new TreeMap<>(new MapKeyComparator());
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
