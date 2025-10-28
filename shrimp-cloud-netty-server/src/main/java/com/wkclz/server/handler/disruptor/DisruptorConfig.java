package com.wkclz.server.handler.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import com.wkclz.server.bean.ReceiveData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executors;

@Configuration
public class DisruptorConfig {

    @Bean
    @ConditionalOnExpression("'${netty.executeDataName}'.equals('disruptor')")
    public Disruptor<ReceiveData> disruptor() {
        ReceiveDataFactory factory = new ReceiveDataFactory();
        int bufferSize = 1024; // RingBuffer 大小
        Disruptor<ReceiveData> disruptor = new Disruptor<>(factory, bufferSize, Executors.defaultThreadFactory());
        // 绑定事件处理器
        disruptor.handleEventsWith(new ExecuteDataHandler());
        disruptor.start();
        return disruptor;
    }

    @Bean("disruptor")
    @ConditionalOnExpression("'${netty.executeDataName}'.equals('disruptor')")
    public DisruptorExecuteData disruptorExecuteData(Disruptor<ReceiveData> disruptor) {
        return new DisruptorExecuteData(disruptor.getRingBuffer());
    }
}
