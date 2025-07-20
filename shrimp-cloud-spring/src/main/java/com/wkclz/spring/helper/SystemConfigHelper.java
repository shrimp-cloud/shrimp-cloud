package com.wkclz.spring.helper;

import com.alibaba.fastjson2.JSON;
import com.wkclz.common.exception.BizException;
import com.wkclz.redis.entity.RedisMsgBody;
import com.wkclz.redis.topic.RedisTopicConfig;
import com.wkclz.spring.config.SpringContextHolder;
import com.wkclz.spring.config.SystemConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
public class SystemConfigHelper {


    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Map<String, String> SYSTEM_CONFIG = null;

    /**
     * 初始化 SYSTEM_CONFIG
     */
    public static void reflash() {
        reflash(SYSTEM_CONFIG);
    }
    public static void reflash(Map<String, String> systemConfigs) {
        if (CollectionUtils.isEmpty(systemConfigs)) {
            throw BizException.error("systemConfigs can not be null or empty!");
        }

        if (!SpringContextHolder.getBean(SystemConfig.class).isCloud()){
            SYSTEM_CONFIG = systemConfigs;
            return;
        }

        RedisMsgBody body = new RedisMsgBody();
        body.setTag(SystemConfigHelper.class.getName());
        body.setMsg(systemConfigs);

        String msg = JSON.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(SpringContextHolder.getBean(RedisTopicConfig.class).getCacheTopic(), msg);
    }

    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw BizException.error("systemConfigs can not be null or empty!");
        }
        Map<String, String> systemConfigs = JSON.parseObject(JSON.toJSONString(msg), Map.class);
        return setLocal(systemConfigs);
    }
    public static boolean setLocal(Map<String, String> systemConfigs) {
        if (CollectionUtils.isEmpty(systemConfigs)) {
            throw BizException.error("systemConfigs can not be null or empty!");
        }
        SYSTEM_CONFIG = systemConfigs;
        return true;
    }

    public static Map<String, String> getLocal() {
        return SYSTEM_CONFIG;
    }

    public static String getSystemConfig(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw BizException.error("key must not be null ot empty!");
        }
        Map<String, String> systemConfigs = getLocal();
        if (systemConfigs == null || systemConfigs.isEmpty()) {
            throw BizException.error("systemConfigs must be init after system start up!");
        }
        String value = systemConfigs.get(key);
        return value;
    }

}