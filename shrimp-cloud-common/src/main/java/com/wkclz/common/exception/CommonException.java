package com.wkclz.common.exception;

import cn.hutool.core.text.CharSequenceUtil;
import com.wkclz.common.emuns.ResultStatus;
import lombok.Data;

/**
 * 业务异常，一般用户可自行调整数据及操作可修复。开发者夫需关心
 * @author shrimp
 */
@Data
public class CommonException extends RuntimeException {

    private static final String DEFAULT_ERR_MSG = "Common error, please turn to Administrator";

    protected Integer code = -1;

    public CommonException() {
        super();
    }

    public CommonException(String msg) {
        super(msg);
    }

    public CommonException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public static CommonException remind(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new CommonException(0, msg);
    }

    public static CommonException error(String msg, Object... params) {
        msg = getMsg(msg, params);
        return new CommonException(-1, msg);
    }

    public static CommonException error(ResultStatus status) {
        String msg = status.getMsg();
        return new CommonException(status.getCode(), msg);
    }


    /**
     * 标准错误返回
     */

    public static CommonException result(Integer code, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new CommonException(code, msg);
    }

    public static CommonException result(ResultStatus status, String msg, Object... params) {
        msg = getMsg(msg, params);
        return new CommonException(status.getCode(), msg);
    }


    protected static String getMsg(String msg, Object... params) {
        boolean isNull = msg == null || msg.trim().isEmpty();
        msg = isNull ? DEFAULT_ERR_MSG : msg;
        if (params == null || params.length == 0) {
            return msg;
        }
        return CharSequenceUtil.format(msg, params);
    }

}
