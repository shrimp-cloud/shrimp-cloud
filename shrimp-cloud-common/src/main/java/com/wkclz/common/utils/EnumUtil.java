package com.wkclz.common.utils;


import com.wkclz.common.annotation.Desc;
import com.wkclz.common.entity.EnumEntity;
import com.wkclz.common.entity.EnumTypeEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * xxx
 *
 * @Aouthor wangkc
 * @Create 2018-06-06 18:14:08
 */
public class EnumUtil {

    private static final Logger logger = LoggerFactory.getLogger(EnumUtil.class);


    private static List<EnumTypeEntity> DICT_TYPE_ENTITYS;


    public static void main(String[] args) {
        List<EnumTypeEntity> dictTypeEntitys = getEnumTypeEntitys("com.wkclz.core.enums");
        dictTypeEntitys.forEach(dictTypeEntity -> {
            System.out.println(dictTypeEntity.getEnumType() + "----->" + dictTypeEntity.getEnumTypeDesc());
        });
        System.out.println("----------------- ## -----------------");
        List<EnumEntity> education = getEnumEntitys("com.wkclz.core.enums", "EDUCATION");
        education.forEach(enumEntity -> {
            System.out.println(enumEntity.getEnumKey() + "----->" + enumEntity.getEnumValue());
        });
    }

    /**
     * 获取所有枚举，以及注释
     *
     * @return
     */
    public static List<EnumTypeEntity> getEnumTypeEntitys(String backPackagePath) {
        Set<Class<?>> classes = ClassUtil.getClasses(backPackagePath);
        DICT_TYPE_ENTITYS = new ArrayList<>();
        for (Class<?> clazz : classes) {
            EnumTypeEntity dictTypeEntity = new EnumTypeEntity();
            dictTypeEntity.setClazz(clazz);
            dictTypeEntity.setEnumType(StringUtil.camelToUnderline(clazz.getSimpleName()).toUpperCase());
            // 判断类上是否有次注解
            boolean anno = clazz.isAnnotationPresent(Desc.class);
            if (anno) {
                // 获取类上的注解
                Desc annotation = (Desc) clazz.getAnnotation(Desc.class);
                // 输出注解上的属性
                dictTypeEntity.setEnumTypeDesc(annotation.value());
            }
            DICT_TYPE_ENTITYS.add(dictTypeEntity);
        }
        return DICT_TYPE_ENTITYS;
    }


    public static List<EnumEntity> getEnumEntitys(String backPackagePath, String type) {
        List<EnumEntity> dictEntities = new ArrayList<>();
        if (type == null) {
            return dictEntities;
        }
        if (DICT_TYPE_ENTITYS == null || DICT_TYPE_ENTITYS.size() == 0) {
            DICT_TYPE_ENTITYS = getEnumTypeEntitys(backPackagePath);
        }
        try {
            for (EnumTypeEntity dictTypeEntity : DICT_TYPE_ENTITYS) {
                if (type.equalsIgnoreCase(dictTypeEntity.getEnumType())) {
                    Class clazz = dictTypeEntity.getClazz();
                    Object[] enumConstants = clazz.getEnumConstants();

                    Method[] declaredMethods = clazz.getDeclaredMethods();
                    Method keyMethod = null;
                    Method valueMethod = null;
                    for (Method method : declaredMethods) {

                        String name = method.getName();
                        if ("getKey".equals(name)) {
                            keyMethod = method;
                        }
                        if ("getValue".equals(name)) {
                            valueMethod = method;
                        }
                    }
                    if (valueMethod == null) {
                        return dictEntities;
                    }
                    for (Object entity : enumConstants) {
                        EnumEntity dictEntity = new EnumEntity();
                        dictEntity.setEnumType(type);
                        dictEntity.setEnumTypeDesc(dictTypeEntity.getEnumTypeDesc());
                        dictEntity.setEnumKey(keyMethod == null ? entity.toString() : keyMethod.invoke(entity).toString());
                        dictEntity.setEnumValue(valueMethod.invoke(entity).toString());
                        dictEntities.add(dictEntity);
                    }
                    break;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return dictEntities;
    }
}
