package com.wkclz.spring.rest;

import com.wkclz.common.entity.Result;
import com.wkclz.common.entity.SystemBaseInfo;
import com.wkclz.common.utils.ServerStateUtil;
import com.wkclz.spring.helper.IpHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@RestController
public class Monitor {


    @GetMapping(Routes.MONITOR_STATUS)
    public Result monitorStatus(){
        return Result.ok();
    }


    /**
     * 获取服务器IP
     * @return
     */
    @GetMapping(Routes.MONITOR_IPS)
    public Result ips(){
        List<Map<String, Object>> serverIps = IpHelper.getServerIps();
        return Result.data(serverIps);
    }

    /**
     * 获取服务器属性
     * @return
     */
    @GetMapping(Routes.MONITOR_PROPERTIES)
    public Result properties(){
        Properties properties = System.getProperties();
        return Result.data(properties);
    }


    @GetMapping(Routes.MONITOR_SERVER_STATE)
    public Result monitorServerState() {
        SystemBaseInfo baseInfo = new SystemBaseInfo();
        baseInfo.setClassLoading(ServerStateUtil.getClassLoadingMXBean());
        baseInfo.setCompilation(ServerStateUtil.getCompilationMXBean());
        baseInfo.setOperatingSystem(ServerStateUtil.getOperatingSystemMXBean());
        baseInfo.setPlatformMBeanServer(ServerStateUtil.getPlatformMBeanServer());
        baseInfo.setRuntime(ServerStateUtil.getRuntimeMXBean());
        baseInfo.setThread(ServerStateUtil.getThreadMXBean());
        baseInfo.setMemory(ServerStateUtil.getMemoryMXBean());
        baseInfo.setMemoryManagers(ServerStateUtil.getMemoryManagerMXBeans());
        baseInfo.setGarbageCollectors(ServerStateUtil.getGarbageCollectorMXBeans());
        baseInfo.setMemoryPools(ServerStateUtil.getMemoryPoolMXBeans());
        baseInfo.setDisks(ServerStateUtil.getDisk());
        return Result.data(baseInfo);
    }

}
