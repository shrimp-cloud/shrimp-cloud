package com.wkclz.common.exception;

import com.wkclz.common.emuns.ResultStatus;

/**
 * 系统异常，一般是设计上的问题。需要优化设计才能解决
 * @author shrimp
 */
public class SysException extends CommonException {

    public SysException() {
        super();
    }

    public SysException(String msg) {
        super(msg);
    }

    public SysException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public static SysException remind(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new SysException(0, msg);
    }

    public static SysException error(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new SysException(-1, msg);
    }

    public static SysException error(ResultStatus status) {
        String msg = status.getMsg();
        return new SysException(status.getCode(), msg);
    }

    /**
     * 标准错误返回
     */

    public static SysException result(Integer code, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new SysException(code, msg);
    }

    public static SysException result(ResultStatus status, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new SysException(status.getCode(), msg);
    }

}
