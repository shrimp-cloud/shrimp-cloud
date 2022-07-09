package com.wkclz.common.exception;

import com.wkclz.common.emuns.ResultStatus;

public class BizException extends RuntimeException {

    private static final String DEFAULT_ERR_MSG = "System error, please turn to Administrator";

    public BizException() {
        super();
    }


    public BizException(String msg) {
        super(msg);
    }

    private Integer code = -1;


    public static BizException remind(String msg, Object... params){
        msg = getMsg(msg, params);
        BizException bizException = new BizException(msg);
        bizException.code = 0;
        return bizException;
    }
    public static BizException error(String msg, Object... params){
        msg = getMsg(msg, params);
        BizException bizException = new BizException(msg);
        bizException.code = -1;
        return bizException;
    }

    public static BizException error(ResultStatus status){
        String msg = status.getMsg();
        BizException bizException = new BizException(msg);
        bizException.code = status.getCode();
        return bizException;
    }


    /**
     * 标准错误返回
     */
    public static BizException result(ResultStatus status, String msg, Object... params){
        msg = getMsg(msg, params);
        BizException bizException = new BizException(status.getMsg());
        bizException.code = status.getCode();
        return bizException;
    }

    public static BizException result(Integer code, String msg){
        BizException bizException = new BizException(msg);
        bizException.code = code;
        return bizException;
    }


    private static String getMsg(String msg, Object... params){
        boolean isNull = msg == null || "".equals(msg.trim());
        msg = isNull ?DEFAULT_ERR_MSG : msg;
        if (params == null || params.length == 0){
            return msg;
        }
        return String.format(msg, params);
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
