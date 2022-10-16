package com.wkclz.rocketmq.config;

import com.wkclz.rocketmq.annotation.RocketMqTag;
import com.wkclz.rocketmq.annotation.RocketMqTopic;
import com.wkclz.rocketmq.bean.RocketMqHandlerInfo;
import com.wkclz.rocketmq.exception.RocketMqBeansException;
import com.wkclz.rocketmq.handler.RocketMqHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
@Configuration
public class RocketMqBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClazz = bean.getClass();
        if (beanClazz.isAnnotationPresent(RocketMqTopic.class)) {
            String topic = ((RocketMqTopic) beanClazz.getAnnotation(RocketMqTopic.class)).value();
            for (Method method : beanClazz.getMethods()) {
                if (method.isAnnotationPresent(RocketMqTag.class)) {
                    String tag = method.getAnnotation(RocketMqTag.class).value();
                    if (tag == null || "".equals(tag.trim())) {
                        tag = "*";
                    }
                    RocketMqHandlerInfo info = RocketMqHandlerFactory.getRocketMqTagHandler(topic, tag);
                    if (null != info) {
                        String msg = "重复定义 消费处理器： %s , 重复位置: %s && %s";
                        String existBean = info.getBean().getClass().getSimpleName();
                        String newBean = bean.getClass().getSimpleName();
                        String format = String.format(msg, (topic + tag), existBean, newBean);
                        throw new RocketMqBeansException(format);
                    }
                    RocketMqHandlerFactory.registerRocketMqTagHandler(bean, topic, method, tag);
                    logger.info("RocketMqHandler Mapped \"{}\" onto {}", (topic + tag), method.toString());
                }
            }
        }
        return bean;
    }

}
