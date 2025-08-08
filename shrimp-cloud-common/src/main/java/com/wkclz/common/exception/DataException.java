package com.wkclz.common.exception;

import com.wkclz.common.emuns.ResultStatus;

/**
 * 数据异常，一般可修改数据，或调整逻辑解决
 * @author shrimp
 */
public class DataException extends CommonException {

    public DataException() {
        super();
    }

    public DataException(String msg) {
        super(msg);
    }

    public DataException(Integer code, String msg) {
        super(code,msg);
    }

    public static DataException remind(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new DataException(0, msg);
    }

    public static DataException error(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new DataException(-1, msg);
    }

    public static DataException error(ResultStatus status) {
        String msg = status.getMsg();
        return new DataException(status.getCode(), msg);
    }

    /**
     * 标准错误返回
     */

    public static DataException result(Integer code, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new DataException(code, msg);
    }

    public static DataException result(ResultStatus status, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new DataException(status.getCode(), msg);
    }

}
