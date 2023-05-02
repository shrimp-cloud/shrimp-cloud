//package com.wkclz.mybatis.config;
//
//import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
//import org.springframework.context.ApplicationListener;
//
///**
// * 解决 druid 报 discard long time none received connection 的问题
// * 默认情况下 druid 使用 ping 方式检测连接是否有效。此处关闭 ping, 即使用 validation-query 做检测
// * @author wangkc
// * @date 2022-05-09
// */
//public class DruidValidPing2Select implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
//
//    @Override
//    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
//        System.setProperty("druid.mysql.usePingMethod", "false");
//    }
//}
