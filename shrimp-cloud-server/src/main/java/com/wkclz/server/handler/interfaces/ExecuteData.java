package com.wkclz.server.handler.interfaces;

import com.wkclz.server.bean.ReceiveData;

/**
 * 数据处理
 */
public interface ExecuteData {
    /**
     * 服务端接受请求后，调用该方法处理数据
     * @param r 数据对象
     */
    void invoke(ReceiveData r);

}
