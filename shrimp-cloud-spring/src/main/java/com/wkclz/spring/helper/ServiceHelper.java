//package com.wkclz.spring.helper;
//
//
//import com.wkclz.common.entity.Result;
//import com.wkclz.common.exception.BizException;
//import com.wkclz.spring.config.Sys;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
///**
// * 服务调用工具
// */
//
//@Component
//public class ServiceHelper {
//
//    private static final Logger logger = LoggerFactory.getLogger(ServiceHelper.class);
//    @Autowired(required = false)
//    private DiscoveryClient client;
//
//    public Result excuteServiceGet(String uri){
//        if (StringUtils.isBlank(uri)){
//            throw BizException.error("request uri can not be null");
//        }
//        String applicationName = Sys.APPLICATION_NAME;
//        List<ServiceInstance> instances = client.getInstances(applicationName);
//        if (CollectionUtils.isEmpty(instances)){
//            throw BizException.error("no instance exist, serviceId is {}", applicationName);
//        }
//        int count = 0;
//        for (ServiceInstance instance : instances) {
//            String url = "http://"+ instance.getHost() + ":" + instance.getPort() + uri;
//            RestTemplate restTemplate = RestTemplateHelper.getRestTemplate();
//
//            Result result;
//            try {
//                result = restTemplate.getForObject(url, Result.class);
//            } catch (Exception e) {
//                logger.warn("can not get {}, with error: {}", url, e.getMessage());
//                continue;
//            }
//
//            if (result.getCode() == 1){
//                count ++;
//            }
//        }
//
//        if (count == instances.size()){
//            return Result.data( count + "个实例调用成功");
//        }
//        return Result.error( instances.size()  + " 个实例在线，但只有 " + count + " 个实例执行成功！" );
//    }
//
//}
