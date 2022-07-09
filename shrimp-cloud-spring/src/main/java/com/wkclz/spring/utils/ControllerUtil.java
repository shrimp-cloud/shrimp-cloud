package com.wkclz.spring.utils;//package com.wkclz.core.util;
//
//import com.sun.javafx.binding.StringFormatter;
//import com.wkclz.core.base.annotation.Desc;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.*;
//
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.*;
//
//public class ControllerUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(ControllerUtil.class);
//
//
//    /**
//     * 获取所有 uri
//     *
//     * @return
//     */
//    public static List<String> getApis(String backPackagePath, Class routerClazz, String module) {
//
//        // 获取路由
//        Map<String, String> routers = getRouters(routerClazz);
//        if (routers == null) {
//            return new ArrayList<>();
//        }
//
//        Map<String, String> controllerMappings = getController(backPackagePath, module);
//
//        List<String> controllers = new ArrayList<>();
//
//        for (Map.Entry<String, String> entry : routers.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            if (controllerMappings.get(key) != null) {
//                value = controllerMappings.get(key) + " // " + value;
//                controllers.add(value);
//            }
//        }
//        return controllers;
//    }
//
//
//    /**
//     * 读取 controller
//     *
//     * @param backPackagePath
//     * @return
//     */
//    public static Map<String, String> getController(String backPackagePath, String module) {
//        Set<Class<?>> classes = ClassUtil.getClasses(backPackagePath);
//        Map<String, String> funs = new HashMap<>();
//
//        if (StringUtils.isBlank(module)) {
//            module = "";
//        }
//
//        for (Class clazz : classes) {
//            // 判断类上是否有次注解
//            boolean anno = clazz.isAnnotationPresent(RestController.class);
//            if (anno) {
//
//                // 大Controller 上的 RequestMapping
//                String preFix = "";
//                boolean hasPreFix = clazz.isAnnotationPresent(RequestMapping.class);
//                if (hasPreFix) {
//                    Annotation annotation = clazz.getAnnotation(RequestMapping.class);
//                    RequestMapping request = (RequestMapping) annotation;
//                    String[] values = request.value();
//                    preFix = values.length == 0 ? null : values[0];
//                }
//
//                // 获取类上的注解
//                Method[] methods = clazz.getDeclaredMethods();
//                for (Method method : methods) {
//                    Annotation[] annotations = method.getAnnotations();
//                    String uri = null;
//                    RequestMethod requestMethod = RequestMethod.GET;
//                    for (Annotation annotation : annotations) {
//                        if (GetMapping.class == annotation.annotationType()) {
//                            GetMapping get = (GetMapping) annotation;
//                            String[] values = get.value();
//                            uri = values.length == 0 ? null : values[0];
//                        }
//                        if (PostMapping.class == annotation.annotationType()) {
//                            PostMapping post = (PostMapping) annotation;
//                            String[] values = post.value();
//                            uri = values.length == 0 ? null : values[0];
//                            requestMethod = RequestMethod.POST;
//                        }
//                        if (RequestMapping.class == annotation.annotationType()) {
//                            RequestMapping request = (RequestMapping) annotation;
//                            String[] values = request.value();
//                            uri = values.length == 0 ? null : values[0];
//                        }
//                        if (null != uri) {
//                            break;
//                        }
//                    }
//                    if (uri != null && uri.length() > 1) {
//                        uri = preFix + uri;
//                        String requestFun;
//                        // 方法名
//                        String funName = uri.substring(1, uri.length());
//                        funName = funName.replaceAll("/", "_");
//                        funName = StringUtil.underlineToCamel(funName);
//                        if (requestMethod == RequestMethod.GET) {
//                            requestFun = StringFormatter.format("export function %s(params) { return request({ url: '%s', method: 'get', params: params }) }", funName, module + uri).getValue();
//                        } else {
//                            requestFun = StringFormatter.format("export function %s(data) { return request({ url: '%s', method: 'post', data: data }) }", funName, module + uri).getValue();
//                        }
//                        funs.put(uri, requestFun);
//                    }
//                }
//
//            }
//        }
//        return funs;
//    }
//
//
//    /**
//     * 获取路由
//     *
//     * @return
//     */
//    public static Map<String, String> getRouters(Class routerClazz) {
//        Map<String, String> map = new HashMap<>();
//        Field[] fields = routerClazz.getDeclaredFields();
//        try {
//            for (Field field : fields) {
//
//                String val = new String();
//                Object o = field.get(val);
//                if (o == null) {
//                    continue;
//                }
//                String value = o.toString();
//                Desc desc = field.getAnnotation(Desc.class);
//                if (desc != null) {
//                    String annoDesc = desc.value();
//                    map.put(value, annoDesc);
//                }
//            }
//        } catch (IllegalAccessException e) {
//            logger.error(e.getMessage(), e);
//        }
//        map = sortMapByKey(map);
//        return map;
//    }
//
//    /**
//     * 排序
//     *
//     * @param map
//     * @return
//     */
//    public static Map<String, String> sortMapByKey(Map<String, String> map) {
//        if (map == null || map.isEmpty()) {
//            return null;
//        }
//        Map<String, String> sortMap = new TreeMap<>(new MapKeyComparator());
//        sortMap.putAll(map);
//        return sortMap;
//    }
//
//    // 仅内部使用
//    private static class MapKeyComparator implements Comparator<String> {
//        @Override
//        public int compare(String str1, String str2) {
//            return str1.compareTo(str2);
//        }
//    }
//}
//
