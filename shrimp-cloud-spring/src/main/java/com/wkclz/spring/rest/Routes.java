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
    String APIS_CODE_V1 = "/apis/code/v1";
    @Desc("3. 接口代码2")
    String APIS_CODE_V2 = "/apis/code/v2";
    @Desc("3. 接口代码3")
    String APIS_CODE_V3 = "/apis/code/v3";


    /**
     * 监控 monitor
     */
    @Desc("1. 监控-redis")
    String MONITOR_REDIS = "/monitor/redis";
    @Desc("2. 监控-服务器IP")
    String MONITOR_IPS = "/monitor/ips";
    @Desc("3. 监控-服务器属性")
    String MONITOR_PROPERTIES = "/monitor/properties";


    // 从接口获取枚举
    String DICT_ITEMS = "/dict/items";


}
