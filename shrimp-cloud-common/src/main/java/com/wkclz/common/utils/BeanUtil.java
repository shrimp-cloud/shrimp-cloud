package com.wkclz.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Description:
 * Created: wangkaicun @ 2018-01-20 下午11:20
 */
public class BeanUtil {

    private final static Logger logger = LoggerFactory.getLogger(BeanUtil.class);
    private final static Map<String, List<PropertyDescriptor>> PROPERTY_DESCRIPTORS = new HashMap<>();

    /**
     * remove the blank string in the  Object
     *
     * @return
     */
    public static <T> T removeBlank(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            List<PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(obj.getClass());
            assert propertyDescriptors != null;
            for (PropertyDescriptor property : propertyDescriptors) {
                Method getter = property.getReadMethod();
                Object value = getter.invoke(obj);
                if (value != null && value.toString().trim().isEmpty()) {
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, new Object[]{null});
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return obj;
    }


    // 获取对象中有值的方法
    public static <T> List<Method> getValuedList(T param){
        List<PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(param.getClass());
        List<Method> list = null;
        assert propertyDescriptors != null;
        for (PropertyDescriptor property : propertyDescriptors) {
            Method getter = property.getReadMethod();
            Object value = null;
            try {
                value = getter.invoke(param);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
            if (value != null) {
                if (list == null){
                    list = new ArrayList<>();
                }
                list.add(getter);
            }
        }
        return list;
    }


    public static List<PropertyDescriptor> getPropertyDescriptors(Class clazz){
        List<PropertyDescriptor> propertyDescriptors = PROPERTY_DESCRIPTORS.get(clazz.getName());
        if (propertyDescriptors != null){
            return propertyDescriptors;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] propertyDescriptorsArr = beanInfo.getPropertyDescriptors();
            List<PropertyDescriptor> list = new ArrayList<>();
            for (PropertyDescriptor propertyDescriptor : propertyDescriptorsArr) {
                list.add(propertyDescriptor);
            }
            PROPERTY_DESCRIPTORS.put(clazz.getName(), list);
            return list;
        } catch (IntrospectionException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * Bean 复制【 copyProperties，效率极低，推荐使用】
     *
     * @param source 源Bean
     * @param <S>    Source
     */
    public static <S> S cpAll(S source) {
        return cp(source, true);
    }
    public static <S> S cpNotNull(S source) {
        return cp(source, false);
    }
    public static <S> S cp(S source, boolean cpoyNull) {
        if (source == null) {
            return null;
        }
        S s;
        try {
            Constructor<?> constructor = source.getClass().getDeclaredConstructor();
            s = (S)constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return cp(source, s, cpoyNull);
    }

    /**
     * Bean 复制【 copyProperties，效率极低，推荐使用】
     *
     * @param source 源Bean
     * @param target 目标Bean
     * @param <S>    Source
     * @param <T>    Target
     */

    public static <S, T> T cpAll(S source, T target) {
        return cp(source, target, true);
    }
    public static <S, T> T cpNotNull(S source, T target) {
        return cp(source, target, false);
    }
    public static <S, T> T cp(S source, T target, boolean cpoyNull) {
        if (source == null || target == null) {
            return null;
        }
        if (cpoyNull) {
            BeanUtils.copyProperties(source, target);
        } else {
            BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        }
        return target;
    }

    /**
     * List Bean 复制【 copyProperties，效率极低，推荐使用】
     *
     * @param source 源ListBean
     * @param <S>    Source
     * @return
     */

    public static <S> List<S> cp(List<S> source) {
        if (source == null) {
            return null;
        }
        if (source.isEmpty()) {
            return new ArrayList<>();
        }
        Class<S> clazz = (Class<S>)source.get(0).getClass();
        return cp(source, clazz);
    }

    public static <S> List<S> cp(List<S> source, Class<S> clazz) {
        if (source == null) {
            return null;
        }
        if (source.isEmpty()) {
            return new ArrayList<>();
        }
        List<S> list = new ArrayList<>();
        try {
            for (S s : source) {
                S t = clazz.getDeclaredConstructor().newInstance();
                cp(s, t, true);
                list.add(t);
            }
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * 找出Bean 中，为 null 的属性
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
