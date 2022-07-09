package com.wkclz.spring.utils;

import com.wkclz.common.annotation.Desc;
import com.wkclz.common.annotation.Router;
import com.wkclz.common.utils.ClassUtil;
import com.wkclz.common.utils.StringUtil;
import com.wkclz.spring.entity.RestInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


public class RestUtil {

    private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);

    /**
     * 读取 controller
     * @return
     */

    public static List<RestInfo> getMapping(String classPrefix) {
        List<RestInfo> mappings = getMapping();
        if (StringUtils.isNotBlank(classPrefix)) {
            mappings = mappings.stream().filter(m->m.getPath().startsWith(classPrefix)).collect(Collectors.toList());
        }
        return mappings;
    }
    public static List<RestInfo> getMapping() {
        List<RestInfo> rests = new ArrayList<>();

        // 获取二级域下的所有 Class
        String clazzName = RestUtil.class.getName();
        int index = clazzName.indexOf(".", clazzName.indexOf(".") + 1);
        String packagePath = clazzName.substring(0, index);
        logger.info("package {} mappings...", packagePath);

        // 筛选出有 Controller 标识的类
        Set<Class<?>> classes = ClassUtil.getClasses(packagePath);
        // Rest 服务类
        List<Class> restClassList = classes.stream().filter(clazz -> clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Controller.class)).collect(Collectors.toList());
        for (Class clazz : restClassList) {

            // 大 Rest 上的 RequestMapping
            String prefix = null;
            boolean hasPreFix = clazz.isAnnotationPresent(RequestMapping.class);
            if (hasPreFix) {
                Annotation annotation = clazz.getAnnotation(RequestMapping.class);
                RequestMapping request = (RequestMapping) annotation;
                String[] values = request.value();
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

            // module
            String module = "NaN";
            String restClassName = clazz.getName();
            int moduleStart = restClassName.indexOf(".", restClassName.indexOf(".") + 1) + 1;
            if (moduleStart > 1 && restClassName.indexOf(".", moduleStart) > moduleStart) {
                module = restClassName.substring(moduleStart, restClassName.indexOf(".", moduleStart));
            }

            // 获取类上的方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
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

                    // 不是rest 接口的注解
                    if (StringUtils.isBlank(uri) || requestMethod == null) {
                        continue;
                    }
                }

                if (uri == null){
                    continue;
                }

                if (!uri.startsWith("/")){
                    uri = "/" + uri;
                }
                if (prefix != null) {
                    uri = prefix + uri;
                }

                // 确定是 rest 接口，提取信息
                if (requestMethod != null) {
                    RestInfo restInfo = new RestInfo();
                    restInfo.setMethod(requestMethod.name());
                    restInfo.setUri(uri);
                    restInfo.setPath(clazz.getName() + "." + method.getName());
                    restInfo.setDesc(desc);
                    restInfo.setModule(module);
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

                    // 方法名
                    String restName = uri.substring(1);
                    restName = restName.replaceAll("/", "_");
                    restName = StringUtil.underlineToCamel(restName);
                    restInfo.setName(restName);
                    rests.add(restInfo);
                }
            }
        }

        // Routers 类
        List<Class> routersClassList = classes.stream().filter(clazz -> clazz.isAnnotationPresent(Router.class)).collect(Collectors.toList());
        Map<String, String> uriDescs = new HashMap<>();
        for (Class routerClazz : routersClassList) {
            Field[] fields = routerClazz.getDeclaredFields();
            try {
                for (Field field : fields) {
                    String val = "";
                    Object o = field.get(val);
                    if (o == null) {
                        continue;
                    }
                    String value = o.toString();
                    Desc desc = field.getAnnotation(Desc.class);
                    if (desc != null) {
                        String annoDesc = desc.value();
                        uriDescs.put(value, annoDesc);
                    }
                }
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
        // 映射新解释
        for (RestInfo rest : rests) {
            String desc = uriDescs.get(rest.getUri());
            if (StringUtils.isNotBlank(desc)){
                rest.setDesc(desc);
            }
        }

        return rests;
    }

}

