package com.wkclz.spring.utils;

import com.alibaba.fastjson2.JSONObject;
import com.wkclz.common.annotation.Desc;
import com.wkclz.common.annotation.Router;
import com.wkclz.common.utils.ClassUtil;
import com.wkclz.common.utils.StringUtil;
import com.wkclz.spring.entity.RestInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class RestUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);

    /**
     * 获取所有接口字符串
     */
    public static String getMappingStr(String appCode, String packagePath, String filter) {
        List<RestInfo> mappings = getMapping(appCode, packagePath, filter);
        String json = JSONObject.toJSONString(mappings);
        return json;
    }
    /**
     * 获取所有接口对象
     */
    public static List<RestInfo> getMapping(String appCode, String packagePath, String filter) {
        List<RestInfo> mappings = null;
        if (StringUtils.isBlank(packagePath)) {
            mappings = getMapping();
        } else {
            mappings = getMapping(packagePath);
        }
        if (StringUtils.isNotBlank(filter)) {
            mappings = mappings.stream().filter(t -> t.getUri().contains(filter)).collect(Collectors.toList());
        }
        mappings.forEach(t -> t.setAppCode(appCode));
        return mappings;
    }


    /**
     * // 默认读取全部
     */
    public static List<RestInfo> getMapping() {
        // 获取二级域下的所有 Class
        String clazzName = RestUtil.class.getName();
        int index = clazzName.indexOf(".", clazzName.indexOf(".") + 1);
        String packagePath = clazzName.substring(0, index);
        List<RestInfo> mappings = getMapping(packagePath);
        return mappings;
    }

    public static List<RestInfo> getMapping(String packagePath) {
        List<RestInfo> rests = new ArrayList<>();
        logger.info("package {} mappings...", packagePath);

        // 筛选出有 Controller 标识的类
        Set<Class<?>> classes = ClassUtil.getClasses(packagePath);
        // Rest 服务类
        List<Class<?>> restClassList = classes.stream().filter(clazz -> clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class)).collect(Collectors.toList());
        for (Class<?> clazz : restClassList) {
            // 大 Rest 上的 RequestMapping
            String prefix = null;
            boolean hasPreFix = clazz.isAnnotationPresent(RequestMapping.class);
            if (hasPreFix) {
                RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                String[] values = annotation.value();
                if (values.length > 0) {
                    prefix = values[0];
                }
            }
            if (prefix != null && !prefix.startsWith("/")) {
                prefix = "/" + prefix;
            }
            if (prefix != null && prefix.endsWith("/")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }

            // 获取类上的方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                RestInfo rest = getRest(method, prefix);
                if (rest != null) {
                    rests.add(rest);
                }
            }
        }

        appendDesc(classes, rests);
        return rests;
    }

    private static RestInfo getRest(Method method, String prefix) {
        if (method == null) {
            return null;
        }
        Annotation[] annotations = method.getAnnotations();
        String uri = null;
        String desc = null;
        RequestMethod requestMethod = null;
        for (Annotation annotation : annotations) {
            if (RequestMapping.class == annotation.annotationType()) {
                RequestMapping request = (RequestMapping) annotation;
                RequestMethod[] requestMethods = request.method();
                requestMethod = requestMethods.length > 0 ? requestMethods[0]:RequestMethod.GET;
                String[] values = request.value();
                uri = values.length == 0 ? null : values[0];
                continue;
            }
            if (GetMapping.class == annotation.annotationType() ) {
                GetMapping request = (GetMapping) annotation;
                requestMethod = RequestMethod.GET;
                String[] values = request.value();
                uri = values.length == 0 ? null : values[0];
                continue;
            }
            if (PostMapping.class == annotation.annotationType()) {
                PostMapping request = (PostMapping) annotation;
                requestMethod = RequestMethod.POST;
                String[] values = request.value();
                uri = values.length == 0 ? null : values[0];
                continue;
            }
            if (PutMapping.class == annotation.annotationType()) {
                PutMapping request = (PutMapping) annotation;
                requestMethod = RequestMethod.PUT;
                String[] values = request.value();
                uri = values.length == 0 ? null : values[0];
                continue;
            }
            if (DeleteMapping.class == annotation.annotationType()) {
                DeleteMapping request = (DeleteMapping) annotation;
                requestMethod = RequestMethod.DELETE;
                String[] values = request.value();
                uri = values.length == 0 ? null : values[0];
                continue;
            }
            // 中文含义
            if (Desc.class == annotation.annotationType()) {
                Desc descAnnto = (Desc) annotation;
                desc = descAnnto.value();
                continue;
            }
        }

        if (uri == null || requestMethod == null ){
            return null;
        }

        if (!uri.startsWith("/")){
            uri = "/" + uri;
        }
        if (prefix != null) {
            uri = prefix + uri;
        }

        // 确定是 rest 接口，提取信息
        RestInfo restInfo = new RestInfo();
        restInfo.setMethod(requestMethod.name());
        restInfo.setUri(uri);
        restInfo.setDesc(desc);
        /*
        restInfo.setReturnType(method.getReturnType());
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 0) {
            List<Class<?>> list = Arrays.stream(parameterTypes).filter(parameterType -> {
                if (parameterType == HttpServletRequest.class) {
                    return false;
                }
                if (parameterType == HttpServletResponse.class) {
                    return false;
                }
                if (parameterType == MultipartFile.class) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            restInfo.setParameterTypes(list);
        }
        */

        // 方法名
        String restName = uri.substring(1);
        restName = restName.replaceAll("/", "_");
        restName = StringUtil.underlineToCamel(restName);
        restInfo.setName(restName);
        return restInfo;
    }

    private static void appendDesc(Set<Class<?>> classes, List<RestInfo> rests) {
        if (CollectionUtils.isEmpty(classes) || CollectionUtils.isEmpty(rests)) {
            return;
        }
        List<Class<?>> routersClassList = classes.stream().filter(clazz -> clazz.isAnnotationPresent(Router.class)).collect(Collectors.toList());
        for (Class<?> routerClazz : routersClassList) {
            Field[] fields = routerClazz.getDeclaredFields();
            try {
                Router routerAnno = routerClazz.getAnnotation(Router.class);
                if (routerAnno != null) {
                    String value = routerAnno.value();
                    if (StringUtils.isNotBlank(value)) {
                        rests.forEach(t-> t.setModule(value));
                    }
                }
                for (Field field : fields) {
                    String val = "";
                    Object o = field.get(val);
                    if (o == null) {
                        continue;
                    }
                    String value = o.toString();
                    // 找到 restInfo
                    List<RestInfo> infos = rests.stream().filter(t -> t.getUri().equals(value)).collect(Collectors.toList());
                    if (infos.size() == 1) {
                        RestInfo restInfo = infos.get(0);
                        Desc desc = field.getAnnotation(Desc.class);
                        if (desc != null) {
                            restInfo.setDesc(desc.value());
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}

