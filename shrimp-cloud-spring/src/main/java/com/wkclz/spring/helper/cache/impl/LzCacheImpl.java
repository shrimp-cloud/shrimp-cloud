//package com.wkclz.spring.helper.cache.impl;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.wkclz.common.exception.BizException;
//import com.wkclz.common.utils.BeanUtil;
//import com.wkclz.redis.topic.RedisTopicConfig;
//import com.wkclz.spring.config.SpringContextHolder;
//import com.wkclz.spring.config.Sys;
//import com.wkclz.spring.constant.Queue;
//import com.wkclz.spring.constant.ServiceIdConstant;
//import com.wkclz.spring.entity.DictType;
//import com.wkclz.spring.helper.DictHelper;
//import com.wkclz.spring.helper.IpHelper;
//import com.wkclz.spring.helper.RestTemplateHelper;
//import com.wkclz.spring.helper.SystemConfigHelper;
//import com.wkclz.spring.helper.cache.LzCache;
//import com.wkclz.spring.rest.Routes;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.URI;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Component
//public class LzCacheImpl implements LzCache {
//
//    private static final Logger logger = LoggerFactory.getLogger(LzCacheImpl.class);
//    private static final Map<String, List> CACHE_CONFIG = new HashMap<>();
//
//    @Autowired(required = false)
//    private DiscoveryClient client;
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//
//    @Override
//    public <T> T get(T param){
//        if (param == null){
//            return null;
//        }
//        String className = param.getClass().getName();
//        List<T> cache = cache(className, null);
//        if (CollectionUtils.isEmpty(cache)){
//            throw BizException.error(className + " has no cache!");
//        }
//
//        List<Method> valuedList = BeanUtil.getValuedList(param);
//        if (valuedList == null) {
//            return null;
//        }
//        try {
//            for (T config : cache) {
//                int fit = 0;
//                for (Method method : valuedList) {
//                    Object configValue = method.invoke(config);
//                    String paramValue = method.invoke(param).toString();
//                    if (configValue != null && paramValue.equals(configValue.toString())){
//                        fit ++;
//                        continue;
//                    }
//                }
//                if (fit == valuedList.size()){
//                    return config;
//                }
//            }
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            logger.error(e.getMessage(), e);
//        }
//        return null;
//    }
//
//
//    @Override
//    public void wipe(Class clazz){
//        String name = clazz.getName();
//        stringRedisTemplate.convertAndSend(SpringContextHolder.getBean(RedisTopicConfig.class).getCacheTopic(),name);
//    }
//
//    // ????????????
//    private synchronized <T> List<T> cache(String clazz, List<T> data){
//        List<T> list = CACHE_CONFIG.get(clazz);
//        if (list != null){
//            return list;
//        }
//        CACHE_CONFIG.put(clazz, data);
//        return CACHE_CONFIG.get(clazz);
//    }
//
//
//    // ????????????
//    public static void clearLocal(String clazz){
//        CACHE_CONFIG.remove(clazz);
//    }
//
//
//
//    @Override
//    public void cache2Local(){
//
//        logger.info("??????????????????????????????????????????...");
//        List<ServiceInstance> instances = client.getInstances(ServiceIdConstant.LZ_SYS);
//        if (CollectionUtils.isEmpty(instances)){
//            logger.warn("have no {} online, please check, systen will be init again autoly if {} is start!", ServiceIdConstant.LZ_SYS, ServiceIdConstant.LZ_SYS);
//            return;
//        }
//
//        ServiceInstance serviceInstance = null;
//        for (ServiceInstance instance : instances) {
//            // ?????????????????????
//            String host = instance.getHost();
//            String serverIp = IpHelper.getServerIp();
//            host = host.substring(0, host.lastIndexOf("."));
//            host = host.substring(0, host.lastIndexOf("."));
//            if (serverIp.contains(host)){
//                serviceInstance = instance;
//                break;
//            }
//        }
//        if (serviceInstance == null){
//            logger.warn("{} ????????????????????????????????????????????????????????????", instances.get(0).getInstanceId());
//            String key = Queue.CACHE_QUEUE_PREFIX + Sys.APPLICATION_GROUP;
//            stringRedisTemplate.opsForList().leftPush(key, System.currentTimeMillis()+"");
//            return;
//        }
//
//        logger.info("????????????????????? {} ????????????...", serviceInstance.getInstanceId());
//
//        // ??????????????????
//        URI uriObj = serviceInstance.getUri();
//        String uri = uriObj.toASCIIString();
//
//        Map<String, String> cacheSysConfig = requestMap(uri + Routes.CACHE_SYS_CONFIG);
//        List<String> cacheAccessUri = requestList(uri + Routes.CACHE_ACCESS_URI, String.class);
//        List<String> cacheApiDomain = requestList(uri + Routes.CACHE_API_DOMAIN, String.class);
//        Map<String, String> cacheTenantDomain = requestMap(uri + Routes.CACHE_TENANT_DOMAIN);
//        List<DictType> cacheSysDict = requestList(uri + Routes.CACHE_SYS_DICT, DictType.class);
//
//        SystemConfigHelper.setLocal(cacheSysConfig);
//        /*
//        AccessHelper.setLocal(cacheAccessUri);
//        ApiDomainHelper.setLocal(cacheApiDomain);
//        TenantDomainHelper.setLocal(cacheTenantDomain);
//        */
//        DictHelper.setLocal(cacheSysDict);
//
//        logger.info("????????????????????? {} ???????????????", serviceInstance.getInstanceId());
//    }
//
//
//    private static String request(String url){
//        RestTemplate restTemplate = RestTemplateHelper.getRestTemplate();
//        JSONObject jsonObject = null;
//        try {
//            jsonObject = restTemplate.getForObject(url, JSONObject.class);
//        } catch (Exception e) {
//            logger.warn("can not request: {}, with error: {}", url, e.getMessage());
//            throw e;
//        }
//        Object code = jsonObject.get("code");
//        Object msg = jsonObject.get("msg");
//        Object data = jsonObject.get("data");
//        if (code == null || !"1".equals(code.toString())){
//            throw BizException.error("????????????: {}", msg);
//        }
//        if (data == null){
//            throw BizException.error("??????????????????????????????: {}", jsonObject);
//        }
//        return JSON.toJSONString(data);
//    }
//
//    private static Map requestMap(String url){
//        String respone = request(url);
//        return JSON.parseObject(respone, Map.class);
//    }
//    private static <T> List<T> requestList(String url, Class<T> clazz){
//        String respone = request(url);
//        List<T> arrray = JSON.parseArray(respone, clazz);
//        return arrray;
//    }
//
//}