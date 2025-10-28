package com.wkclz.server.handler.disruptor;

import cn.hutool.extra.spring.SpringUtil;
import com.lmax.disruptor.EventHandler;
import com.wkclz.server.bean.ReceiveData;
import com.wkclz.server.handler.base.DefaultExecuteData;

public class ExecuteDataHandler implements EventHandler<ReceiveData> {
    @Override
    public void onEvent(ReceiveData receiveData, long l, boolean b) throws Exception {
        SpringUtil.getBean(DefaultExecuteData.class).invoke(receiveData);
    }
}
