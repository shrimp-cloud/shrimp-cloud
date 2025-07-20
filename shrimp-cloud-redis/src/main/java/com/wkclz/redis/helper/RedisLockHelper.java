package com.wkclz.redis.helper;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.wkclz.common.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author wangkc
 * @date 2020-04-05 13:22
 */
@Component
public class RedisLockHelper {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockHelper.class);
    private static final String REDIS_LOCK_PREFIS = "lock:";

    @Autowired(required = false)
    private RedisTemplate redisTemplate;


    /**
     * redis 锁，
     *
     * @param key
     * @return true, 加锁成功，可以继续， false, 加锁失败，需要等待
     */
    public boolean lock(String key) {
        int randomInt = RandomUtil.randomInt(1, 10);
        return lock(key, 60 - randomInt);
    }


    /**
     * redis 锁，
     *
     * @param key
     * @param second
     * @return true, 加锁成功，可以继续， false, 加锁失败，需要等待
     */
    public boolean lock(String key, Integer second) {
        if (redisTemplate == null) {
            throw BizException.error("no redis support");
        }
        if (StringUtils.isBlank(key)) {
            throw BizException.error("key can not be null");
        }
        if (second == null) {
            throw BizException.error("second can not be null");
        }
        long currentTimeMillis = System.currentTimeMillis();
        key = getKey(key);
        boolean boo = redisTemplate.opsForValue().setIfAbsent(key, currentTimeMillis + "", second, TimeUnit.SECONDS);
        if (!boo) {
            Object o = redisTemplate.opsForValue().get(key);
            if (o == null) {
                throw BizException.error("found lock {}, but can not found value!", key);
            }
            long aLong = Long.parseLong(o.toString());
            Date date = new Date(aLong);
            logger.warn("lock {} faild, it has rocked @ {}", key, DateUtil.format(date, "yyyy-M-dd HH:mm:ss"));
        }
        return boo;
    }


    /**
     * 解锁
     *
     * @param key
     * @return
     */
    public boolean unlock(String key) {
        if (redisTemplate == null) {
            throw BizException.error("no redis support");
        }
        if (StringUtils.isBlank(key)) {
            throw BizException.error("key can not be null");
        }
        key = getKey(key);
        Boolean delete = redisTemplate.delete(key);
        return Boolean.TRUE.equals(delete);
    }


    private static String getKey(String key) {
        return REDIS_LOCK_PREFIS + ":"  + key;
    }


}
