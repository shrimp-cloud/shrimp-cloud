package com.wkclz.mqtt.config;

import com.wkclz.mqtt.annotation.MqttController;
import com.wkclz.mqtt.annotation.MqttTopicMapping;
import com.wkclz.mqtt.exception.MqttBeansException;
import com.wkclz.mqtt.handler.MqttHandlerFactory;
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
public class MqttBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MqttBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClazz = bean.getClass();
        if (beanClazz.isAnnotationPresent(MqttController.class)) {
            String parentTopic = ((MqttController) beanClazz.getAnnotation(MqttController.class)).value();
            MqttHandlerFactory.getParentTopicSet().add(parentTopic);
            for (Method method : beanClazz.getMethods()) {
                if (method.isAnnotationPresent(MqttTopicMapping.class)) {
                    String subTopic = method.getAnnotation(MqttTopicMapping.class).value();
                    String realTopic;
                    if ("".equals(subTopic)) {
                        realTopic = parentTopic + "/#";
                    } else {
                        realTopic = (parentTopic + "/" + subTopic);
                    }
                    realTopic = realTopic.replaceAll("/+", "/");
                    if (realTopic.endsWith("/")) {
                        realTopic = realTopic.substring(0, realTopic.length() - 1);
                    }
                    if (null != MqttHandlerFactory.getMqttHandler(realTopic)) {
                        throw new MqttBeansException(bean.getClass().getSimpleName() + " topic 重复定义,值为" + realTopic);
                    }
                    MqttHandlerFactory.registerMqttHandler(realTopic, method);
                    MqttHandlerFactory.registerMqttController(realTopic, bean);
                    logger.info("MqttHandler Mapped \"{}\" onto {}", realTopic, method.toString());
                }
            }
        }
        return bean;
    }

}
