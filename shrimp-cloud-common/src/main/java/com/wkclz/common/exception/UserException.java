package com.wkclz.common.exception;

import com.wkclz.common.emuns.ResultStatus;

/**
 * 用户异常，一般用户可自行调整数据及操作可修复。开发者夫需关心
 * @author shrimp
 */
public class UserException extends CommonException {

    public UserException() {
        super();
    }

    public UserException(String msg) {
        super(msg);
    }

    public UserException(Integer code, String msg) {
        super(msg);
    }


    public static UserException remind(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new UserException(0, msg);
    }

    public static UserException error(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new UserException(-1, msg);
    }

    public static UserException error(ResultStatus status) {
        String msg = status.getMsg();
        return new UserException(status.getCode(), msg);
    }

    /**
     * 标准错误返回
     */

    public static UserException result(Integer code, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new UserException(code, msg);
    }

    public static UserException result(ResultStatus status, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new UserException(status.getCode(), msg);
    }

}
