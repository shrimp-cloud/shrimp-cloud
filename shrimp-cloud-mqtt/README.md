# 整体实现思路

- 自定义注解MqttController，MqttTopicMapping，MqttMessageId，MqttMessageBody；
- 利用BeanPostProcessor，获得所有注解了MqttController的bean及其注解值，获得其所有注解了MqttTopicMapping的Method方法及其注解值，利用两者的注解值作为其key，分别将bean,Method为value放入不同的map中，记录所有注解了MqttController的注解值作为下一步需要订阅的Topic；
- 利用ApplicationListener在所有bean加载完成后使用实例化的mqConsumer来订阅所有需要订阅的Topic；
- 在mq订阅的处理方法中，根据消息的全Topic在上述步骤的map中获得其对应的bean和Method，同时根据MqttMessageId，MqttMessageBody来设置相关参数，使用method.invoke(owner, paramValues);实现方法的调用，来达到消息的处理分发。
