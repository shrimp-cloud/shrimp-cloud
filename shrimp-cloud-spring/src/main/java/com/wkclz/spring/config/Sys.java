package com.wkclz.spring.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.wkclz.spring.enums.EnvType;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 系统启动后执行一次
 * Created: wangkaicun @ 2017-10-18 下午10:17
 */

@Component
public class Sys implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(Sys.class);


    // default DEV 当前启动的系统环境【初始为 DEV】
    private static final AtomicReference<EnvType> CURRENT_ENV = new AtomicReference<>(EnvType.DEV);
    // Application GROUP 系统启动后会修改
    private static final AtomicReference<String> APPLICATION_GROUP = new AtomicReference<>("CLOUD");
    // Application Name 系统启动后会修改
    private static final AtomicReference<String> APPLICATION_NAME = new AtomicReference<>("APP");
    // default now, it will be changed by main class 系统启动时间
    private static final AtomicLong STARTUP_DATE = new AtomicLong(System.currentTimeMillis());
    // system start up success confirm 系统启动确认
    public static final AtomicBoolean SYSTEM_START_UP_CONFIRM = new AtomicBoolean(false);

    @Resource
    private SystemConfig systemConfig;


    @Override
    public void run(ApplicationArguments args) {
        // 初始化环境信息
        initEnv();

        // 使用到再说
        // initCache();
    }

    private void initEnv() {
        ApplicationContext applicationContext = SpringContextHolder.getApplicationContext();
        Environment env = applicationContext.getEnvironment();
        String[] activeProfiles = env.getActiveProfiles();

        for (String profile : activeProfiles) {
            profile = profile.toUpperCase();
            EnvType envType = EnvType.DEV;

            if (profile.contains(EnvType.PROD.toString())) {
                envType = EnvType.PROD;
            }
            if (profile.contains(EnvType.UAT.toString())) {
                envType = EnvType.UAT;
            }
            if (profile.contains(EnvType.SIT.toString())) {
                envType = EnvType.PROD;
            }
            if (profile.contains(EnvType.DEV.toString())) {
                envType = EnvType.DEV;
            }

            CURRENT_ENV.set(envType);
        }

        // set startupDate for the whole system
        long startupDate = applicationContext.getStartupDate();

        // 初始化信息，需要应用名做前缀
        APPLICATION_NAME.set(systemConfig.getApplicationName());
        String group = systemConfig.getApplicationGroup();
        group = (group == null || group.isEmpty()) ? APPLICATION_NAME.get() : group;
        APPLICATION_GROUP.set(group.toUpperCase().replace("-", "_"));

        STARTUP_DATE.set(startupDate);
        SYSTEM_START_UP_CONFIRM.set(true);
        String date = DateUtil.format(new Date(startupDate), "yyyy-MM-dd HH:mm:ss");
        logger.info("===================>  System is start up as {} @ {}", CURRENT_ENV.get(), date);
    }

    private void initCache() {
        ThreadUtil.execAsync(() -> ThreadUtil.sleep(1, TimeUnit.SECONDS),false);
        // lzCache.cache2Local();
        logger.info("run {} over", this.getClass());
    }

    public static EnvType getCurrentEnv() {
        return CURRENT_ENV.get();
    }
    public static String getApplicationGroup() {
        return APPLICATION_GROUP.get();
    }
    public static String getApplicationName() {
        return APPLICATION_NAME.get();
    }
    public static Long getStartupDate() {
        return STARTUP_DATE.get();
    }
    public static boolean getSystemStartUpConfirm() {
        return SYSTEM_START_UP_CONFIRM.get();
    }



}
