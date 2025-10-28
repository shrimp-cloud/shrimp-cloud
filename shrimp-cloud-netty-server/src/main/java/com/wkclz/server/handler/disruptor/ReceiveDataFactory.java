package com.wkclz.server.handler.disruptor;

import com.lmax.disruptor.EventFactory;
import com.wkclz.server.bean.ReceiveData;

public class ReceiveDataFactory implements EventFactory<ReceiveData> {
    @Override
    public ReceiveData newInstance() {
        return ReceiveData.builder().build();
    }
}