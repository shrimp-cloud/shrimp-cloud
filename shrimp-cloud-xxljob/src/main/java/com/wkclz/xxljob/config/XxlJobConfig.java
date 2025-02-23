package com.wkclz.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author shrimp
 */
@Slf4j
@Component
public class XxlJobConfig {

    // 调度中心部署根地址 [选填]：如调度中心集群部署存在多个地址则用逗号分隔。执行器将会使用该地址进行"执行器心跳注册"和"任务结果回调"；为空则关闭自动注册；
    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;

    // 调度中心通讯TOKEN [选填]：非空时启用；
    @Value("${xxl.job.admin.accessToken:default_token}")
    private String accessToken;

    // 调度中心通讯超时时间[选填]，单位秒；默认3s；
    @Value("${xxl.job.admin.timeout:3}")
    private Integer timeout;

    // 执行器AppName [选填]：执行器心跳注册分组依据；为空则关闭自动注册
    @Value("${xxl.job.executor.appname:${spring.application.name}}")
    private String appName;

    // 执行器注册 [选填]：优先使用该配置作为注册地址，为空时使用内嵌服务 ”IP:PORT“ 作为注册地址。从而更灵活的支持容器类型执行器动态IP和动态映射端口问题。
    @Value("${xxl.job.executor.address:}")
    private String address;

    // 执行器IP [选填]：默认为空表示自动获取IP，多网卡时可手动设置指定IP，该IP不会绑定Host仅作为通讯使用；地址信息用于 "执行器注册" 和 "调度中心请求并触发任务"；
    @Value("${xxl.job.executor.ip:}")
    private String ip;

    // 执行器端口号 [选填]：小于等于0则自动获取；默认端口为9999，单机部署多个执行器时，注意要配置不同执行器端口；
    @Value("${xxl.job.executor.port:9999}")
    private Integer port;

    // 执行器运行日志文件存储磁盘路径 [选填] ：需要对该路径拥有读写权限；为空则使用默认路径；
    @Value("${xxl.job.executor.logpath:./xxl-job/jobhandler}")
    private String logPath;

    // 执行器日志文件保存天数 [选填] ： 过期日志自动清理, 限制值大于等于3时生效; 否则, 如-1, 关闭自动清理功能；
    @Value("${xxl.job.executor.logretentiondays:30}")
    private Integer logRetentionDays;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init {} to {}", appName, adminAddresses);
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appName);
        executor.setTimeout(timeout);
        executor.setAddress(address);
        executor.setIp(ip);
        executor.setPort(port);
        executor.setAccessToken(accessToken);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
        return executor;
    }

}