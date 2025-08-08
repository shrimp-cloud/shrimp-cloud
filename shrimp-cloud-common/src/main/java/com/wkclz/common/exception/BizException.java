package com.wkclz.common.exception;

import com.wkclz.common.emuns.ResultStatus;

/**
 * 业务异常，一般用户可自行调整数据及操作可修复。开发者夫需关心
 * @author shrimp
 */
public class BizException extends CommonException {

    public BizException() {
        super();
    }

    public BizException(String msg) {
        super(msg);
    }

    public BizException(Integer code, String msg) {
        super(msg);
    }


    public static BizException remind(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new BizException(0, msg);
    }

    public static BizException error(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new BizException(-1, msg);
    }

    public static BizException error(ResultStatus status) {
        String msg = status.getMsg();
        return new BizException(status.getCode(), msg);
    }

    /**
     * 标准错误返回
     */

    public static BizException result(Integer code, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new BizException(code, msg);
    }

    public static BizException result(ResultStatus status, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new BizException(status.getCode(), msg);
    }

}
