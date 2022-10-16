package com.wkclz.rocketmq.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
public class RocketMqConfig {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqConfig.class);

    @Value("${shrimp.cloud.rocketmq.endpoint:}")
    private String endpoint;
    @Value("${shrimp.cloud.rocketmq.consumer-group:}")
    private String consumerGroup;
    @Value("${spring.application.name:}")
    private String applicationName;

    public String getConsumerGroup() {
        if (StringUtils.isBlank(consumerGroup)) {
            consumerGroup = applicationName;
        }
        return consumerGroup;
    }

}