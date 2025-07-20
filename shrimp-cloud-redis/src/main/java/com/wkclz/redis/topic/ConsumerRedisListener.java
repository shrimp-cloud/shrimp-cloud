package com.wkclz.redis.topic;

import com.alibaba.fastjson2.JSON;
import com.wkclz.common.exception.BizException;
import com.wkclz.redis.entity.RedisMsgBody;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ConsumerRedisListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerRedisListener.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        doBusiness(message);
    }

    /**
     * 打印 message body 内容
     *
     * @param message
     */
    public void doBusiness(Message message) {
        Object value = stringRedisTemplate.getValueSerializer().deserialize(message.getBody());
        if (value == null){
            throw BizException.error("topic body is null: {}", message);
        }
        logger.info("consumer message: {}", value);
        
        String redisMsgBodyStr = value.toString();
        RedisMsgBody redisMsgBody = JSON.parseObject(redisMsgBodyStr, RedisMsgBody.class);

        String tag = redisMsgBody.getTag();
        Object msg = redisMsgBody.getMsg();


        boolean result = false;
        /*
        if (AccessHelper.class.getName().equals(tag)){
            result = AccessHelper.setLocal(msg);
        }
        if (ApiDomainHelper.class.getName().equals(tag)){
           result = ApiDomainHelper.setLocal(msg);
        }
        if (SystemConfigHelper.class.getName().equals(tag)){
            result = SystemConfigHelper.setLocal(msg);
        }
        if (TenantDomainHelper.class.getName().equals(tag)){
            result = TenantDomainHelper.setLocal(msg);
        }
        if (DictHelper.class.getName().equals(tag)){
            result = DictHelper.setLocal(msg);
        }
        */

        if (result){
            logger.info("consumer {} message finish", tag);
        } else {
            logger.error("no progress consume this msg {}", tag);
        }
        // LzCacheImpl.clearLocal(value.toString());
    }

}
