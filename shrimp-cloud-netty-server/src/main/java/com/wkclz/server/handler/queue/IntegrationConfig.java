package com.wkclz.server.handler.queue;

import com.wkclz.server.config.NettyConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class IntegrationConfig {

    @Bean
    @ConditionalOnExpression("'${netty.executeDataName}'.equals('queue')")
    public MessageChannel messageChannel(NettyConfig nettyConfig) {
        return new QueueChannel(nettyConfig.getCapacity());
    }

}
