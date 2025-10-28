package com.wkclz.server.handler.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.wkclz.server.bean.ReceiveData;
import com.wkclz.server.handler.interfaces.ExecuteData;

public class DisruptorExecuteData implements ExecuteData {
    private final RingBuffer<ReceiveData> ringBuffer;
    public DisruptorExecuteData(RingBuffer<ReceiveData> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ReceiveData r) {
        long sequence = ringBuffer.next();  // 获取下一个序列号
        try {
            ReceiveData receiveData = ringBuffer.get(sequence); // 获取事件对象
            receiveData.setClientDto(r.getClientDto());
            receiveData.setServerDto(r.getServerDto());
            receiveData.setId(r.getId());
            receiveData.setAppName(r.getAppName());
            receiveData.setHandleStatus(r.getHandleStatus());
            receiveData.setReqData(r.getReqData());
            receiveData.setReqStr(r.getReqStr());
            receiveData.setReqTime(r.getReqTime());
            receiveData.setCtx(r.getCtx());
        } finally {
            ringBuffer.publish(sequence);  // 发布事件
        }

    }

    @Override
    public void invoke(ReceiveData r) {
        onData(r);
    }
}
