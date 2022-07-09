package com.wkclz.spring.rest;

import com.wkclz.common.annotation.Desc;
import com.wkclz.common.annotation.Router;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-19 上午12:46
 */
@Router("core 包路由")
public interface Routes {

    /**
     * 一个接口组对应一个 Controller
     */

    @Desc("1. 下载API 信息")
    String APIS_LIST = "/apis/list";
    @Desc("2. 接口代码1")
    String APIS_CODE_v1 = "/apis/code/v1";
    @Desc("3. 接口代码2")
    String APIS_CODE_v2 = "/apis/code/v2";
    @Desc("3. 接口代码3")
    String APIS_CODE_v3 = "/apis/code/v3";


    /**
     * 监控 monitor
     */
    @Desc("1. 监控-redis")
    String MONITOR_REDIS = "/monitor/redis";
    @Desc("2. 监控-服务器IP")
    String MONITOR_IPS = "/monitor/ips";
    @Desc("3. 监控-服务器属性")
    String MONITOR_PROPERTIES = "/monitor/properties";


    /**
     * 获取Cache
     */
    @Desc("0. 缓存-刷新全局缓存")
    String CACHE_REFRESH = "/cache/refresh";
    @Desc("1. 缓存-sysConfig")
    String CACHE_SYS_CONFIG = "/cache/sys/config";
    @Desc("2. 缓存-apiDomain")
    String CACHE_API_DOMAIN = "/cache/api/domain";
    @Desc("3. 缓存-tenantDomain")
    String CACHE_TENANT_DOMAIN = "/cache/tenant/domain";
    @Desc("4. 缓存-accessUri")
    String CACHE_ACCESS_URI = "/cache/access/uri";
    @Desc("1. 缓存-Dict")
    String CACHE_SYS_DICT = "/cache/sys/dict";



}
