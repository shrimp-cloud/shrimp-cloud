package com.wkclz.server.handler.queue;

import com.wkclz.server.bean.ReceiveData;
import com.wkclz.server.handler.base.DefaultExecuteData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnExpression("'${netty.executeDataName}'.equals('queue')")
public class IntegrationHandler {

    @Autowired
    private DefaultExecuteData defaultExecuteData;

    @ServiceActivator(inputChannel = "messageChannel")
    public void handleMessage(ReceiveData r){
        defaultExecuteData.invoke(r);
    }

}
