package com.wkclz.common.utils;

import com.wkclz.common.entity.BaseEntity;
import com.wkclz.common.entity.FieldInfo;
import com.wkclz.common.exception.BizException;
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
import java.lang.reflect.Field;
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


    // BaseEntity 字段缓存
    private static List<String> BASE_ENTITY_FIELD = null;
    private static final Map<Class<?>, Map<String, FieldInfo>> CLASS_METHOD_CACHE = new HashMap<>();

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
    public static <T> List<Method> getValuedList(T param) {
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
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(getter);
            }
        }
        return list;
    }


    public static List<PropertyDescriptor> getPropertyDescriptors(Class clazz) {
        List<PropertyDescriptor> propertyDescriptors = PROPERTY_DESCRIPTORS.get(clazz.getName());
        if (propertyDescriptors != null) {
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
            s = (S) constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
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
        Class<S> clazz = (Class<S>) source.get(0).getClass();
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
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
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


    /**
     * 从业务实体中，获取业务字段的 getter 方法
     */
    public static <T extends BaseEntity> Map<String, FieldInfo> getGetters(Class<T> clazz) {
        if (clazz == null) {
            throw BizException.error("getBizFields clazz can not be null");
        }

        Map<String, FieldInfo> cachedMethods = CLASS_METHOD_CACHE.get(clazz);
        if (cachedMethods != null) {
            return cachedMethods;
        }

        synchronized (clazz) {
            cachedMethods = CLASS_METHOD_CACHE.get(clazz);
            if (cachedMethods != null) {
                return cachedMethods;
            }

            Class<? super T> superclass = clazz.getSuperclass();
            Field[] superFields = superclass.getDeclaredFields();
            Method[] superMethods = superclass.getDeclaredMethods();

            Field[] declaredFields = clazz.getDeclaredFields();
            Method[] declaredMethods = clazz.getDeclaredMethods();

            List<Field> fields = new ArrayList<>();
            fields.addAll(Arrays.asList(superFields));
            fields.addAll(Arrays.asList(declaredFields));

            List<Method> methods = new ArrayList<>();
            methods.addAll(Arrays.asList(superMethods));
            methods.addAll(Arrays.asList(declaredMethods));

            List<String> baseEntityField = getBaseEntityField();
            Map<String, FieldInfo> getters = new HashMap<>();
            for (Field field : fields) {
                String name = field.getName();
                if (baseEntityField.contains(field.getName())) {
                    continue;
                }
                String getter = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                for (Method method : methods) {
                    if (getter.equals(method.getName())) {
                        FieldInfo info = new FieldInfo();
                        info.setFileName(name);
                        info.setGetter(method);
                        info.setFileClass(field.getType());
                        getters.put(name, info);
                    }
                }
            }
            CLASS_METHOD_CACHE.put(clazz, getters);
            return getters;
        }
    }


    /**
     * 获取 BaseEntity 的字段，方便在业务实体中排除
     */
    private static List<String> getBaseEntityField() {
        if (BASE_ENTITY_FIELD != null) {
            return BASE_ENTITY_FIELD;
        }
        synchronized (BaseEntity.class) {
            if (BASE_ENTITY_FIELD != null) {
                return BASE_ENTITY_FIELD;
            }
            // List<String> extFields = Arrays.asList("id", "sort", "remark", "version");
            Field[] declaredFields = BaseEntity.class.getDeclaredFields();
            // BASE_ENTITY_FIELD = Arrays.stream(declaredFields).map(Field::getName).filter(t -> !extFields.contains(t)).toList();
            BASE_ENTITY_FIELD = Arrays.stream(declaredFields).map(Field::getName).toList();
            return BASE_ENTITY_FIELD;
        }
    }

}
