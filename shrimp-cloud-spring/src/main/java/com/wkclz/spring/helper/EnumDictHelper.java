package com.wkclz.spring.helper;


import com.wkclz.common.utils.ClassUtil;
import com.wkclz.common.utils.StringUtil;
import com.wkclz.spring.entity.EnumDict;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class EnumDictHelper {

    private static Map<String, List<EnumDict>> ENUM_DICT_MAP = new HashMap<>();

    public static void init() {
        // 筛选出有 Controller 标识的类
        Set<Class<?>> classes = ClassUtil.getClasses("com.wkclz");

        for (Class<?> clazz : classes) {
            // 不是 enums， 跳过
            if (Enum.class != clazz.getSuperclass()) {
                continue;
            }
            boolean value = false;
            boolean label = false;
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                if ("value".equals(f.getName())) {
                    value = true;
                }
                if ("label".equals(f.getName())) {
                    label = true;
                }
            }
            // 没有 value 或 label
            if (!value || !label) {
                continue;
            }

            String simpleName = clazz.getSimpleName();
            simpleName = StringUtil.camelToUnderline(simpleName).toUpperCase();
            List<EnumDict> dicts = new ArrayList<>();
            try {
                Object[] enumConstants = clazz.getEnumConstants();
                Method valueGetter = clazz.getMethod("getValue");
                Method labelGetter = clazz.getMethod("getLabel");
                for (Object enumConstant : enumConstants) {
                    EnumDict dict = new EnumDict();
                    dict.setType(simpleName);
                    dict.setValue(valueGetter.invoke(enumConstant));
                    dict.setLabel(labelGetter.invoke(enumConstant));
                    dicts.add(dict);
                }
            } catch (Exception e) {
                // who care ?;
            }
            ENUM_DICT_MAP.put(simpleName, dicts);
        }
    }

    public static List<EnumDict> get(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return ENUM_DICT_MAP.get(name);
    }

    public static void main(String[] args) {
        init();
    }
}

