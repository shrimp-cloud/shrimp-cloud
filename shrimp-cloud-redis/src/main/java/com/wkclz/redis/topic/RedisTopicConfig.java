package com.wkclz.redis.topic;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

// 消息发布：stringRedisTemplate.convertAndSend("string-topic", msg);
// 所有订阅者均会消费一次

@Configuration
public class RedisTopicConfig {

    private static final String CACHE_CONFIG_TOPIC_SUB = "cache-config-topic";
    private static String CACHE_CONFIG_TOPIC = null;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public ConsumerRedisListener consumerRedis() {
        return new ConsumerRedisListener();
    }
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic(getCacheTopic());
    }
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(consumerRedis(), topic());
        return container;
    }

    public String getCacheTopic(){
        if (CACHE_CONFIG_TOPIC != null){
            return CACHE_CONFIG_TOPIC;
        }
        synchronized (this){
            CACHE_CONFIG_TOPIC = CACHE_CONFIG_TOPIC_SUB;
        }
        return CACHE_CONFIG_TOPIC;
    }

}
