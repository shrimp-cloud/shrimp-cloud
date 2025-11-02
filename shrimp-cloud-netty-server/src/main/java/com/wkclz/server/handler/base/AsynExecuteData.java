package com.wkclz.server.handler.base;

import cn.hutool.core.thread.ThreadUtil;
import com.wkclz.server.bean.ReceiveData;
import com.wkclz.server.handler.interfaces.ExecuteData;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component("asyn")
public class AsynExecuteData implements ExecuteData {

    @Resource
    private DefaultExecuteData defaultExecuteData;
    @Override
    public void invoke(ReceiveData r) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                defaultExecuteData.invoke(r);
            }
        });
    }
}
