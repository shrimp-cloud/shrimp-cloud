package com.wkclz.rocketmq.config;

import com.wkclz.rocketmq.bean.RocketMqHandlerInfo;
import com.wkclz.rocketmq.bean.RocketMqMsg;
import com.wkclz.rocketmq.handler.RocketMqHandlerFactory;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Configuration
public class RocketMqApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqApplicationListener.class);

    @Autowired(required = false)
    private RocketMqConfig rocketMqConfig;;

    @Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

        Map<String, RocketMqHandlerInfo> handlerInfos = RocketMqHandlerFactory.getRocketMqTags();
        if (CollectionUtils.isEmpty(handlerInfos)) {
			logger.warn("当前应用并未有任何topic订阅");
			return;
		}

        String endpoint = rocketMqConfig.getEndpoint();
        String consumerGroup = rocketMqConfig.getConsumerGroup();

        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder().setEndpoints(endpoint).build();

		//订阅所有 topic-tag，再根据 分发
        for (Map.Entry<String, RocketMqHandlerInfo> entry : handlerInfos.entrySet()) {
			logger.info("Add a new mq subscription, topic-tag:{}", entry.getKey());
            RocketMqHandlerInfo info = entry.getValue();
            FilterExpression filterExpression = new FilterExpression(info.getTag(), FilterExpressionType.TAG);

            try {
                provider.newPushConsumerBuilder()
                    .setClientConfiguration(clientConfiguration)
                    //设置消费者分组。
                    .setConsumerGroup(consumerGroup)
                    //设置预绑定的订阅关系。
                    .setSubscriptionExpressions(Collections.singletonMap(info.getTopic(), filterExpression))
                    //设置消费监听器。
                    .setMessageListener(messageView -> {
                        RocketMqMsg msg = new RocketMqMsg();
                        msg.setMessageId(messageView.getMessageId().toString());
                        msg.setTopic(messageView.getTopic());
                        msg.setTag(messageView.getTag().get());
                        msg.setKeys(List.copyOf(messageView.getKeys()));
                        msg.setPayload(msg.getPayload());

                        logger.info("customer:messageId: {}, topic:{}, tag: {}",
                            msg.getMessageId(),
                            msg.getTopic(),
                            msg.getTag()
                        );

                        Method method = info.getMethod();
                        try {
                            method.invoke(msg);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                        return ConsumeResult.SUCCESS;
                    })
                    .build();
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
        }
		logger.info("MqConsumer Started");
	}


}
