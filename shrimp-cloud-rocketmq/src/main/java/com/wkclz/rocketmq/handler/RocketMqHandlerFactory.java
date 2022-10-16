package com.wkclz.rocketmq.handler;

import com.wkclz.rocketmq.bean.RocketMqHandlerInfo;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangkaicun
 * @date 2022-04-08
 */
public class RocketMqHandlerFactory {

	private static Map<String, RocketMqHandlerInfo> rocketMqTagHandlers = new HashMap<>();


	public static void registerRocketMqTagHandler(Object bean, String topic, Method method, String tag) {
        if (tag == null || "".equals(tag.trim())) {
            tag = "*";
        }
        String key = topic + "/" + tag;
        RocketMqHandlerInfo info = new RocketMqHandlerInfo();
        info.setBean(bean);
        info.setTopic(topic);
        info.setMethod(method);
        info.setTag(tag);
        rocketMqTagHandlers.put(key, info);
	}

    public static Map<String, RocketMqHandlerInfo> getRocketMqTags() {
        return rocketMqTagHandlers;
    }

    public static RocketMqHandlerInfo getRocketMqTagHandler(String topic) {
        return rocketMqTagHandlers.get(topic + "/*");
    }

	public static RocketMqHandlerInfo getRocketMqTagHandler(String topic, String tag) {
        if (tag == null || "".equals(tag.trim())) {
            tag = "*";
        }
        String key = topic + "/" + tag;
		return rocketMqTagHandlers.get(key);
	}
}
