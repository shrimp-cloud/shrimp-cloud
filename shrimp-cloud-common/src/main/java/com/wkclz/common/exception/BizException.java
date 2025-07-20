package com.wkclz.common.exception;

import cn.hutool.core.text.CharSequenceUtil;
import com.wkclz.common.emuns.ResultStatus;

public class BizException extends RuntimeException {

    private static final String DEFAULT_ERR_MSG = "System error, please turn to Administrator";

    private Integer code = -1;

    public BizException() {
        super();
    }

    public BizException(String msg) {
        super(msg);
    }

    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
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

    private static String getMsg(String msg, Object... params) {
        boolean isNull = msg == null || "".equals(msg.trim());
        msg = isNull ? DEFAULT_ERR_MSG : msg;
        if (params == null || params.length == 0) {
            return msg;
        }
        return CharSequenceUtil.format(msg, params);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
