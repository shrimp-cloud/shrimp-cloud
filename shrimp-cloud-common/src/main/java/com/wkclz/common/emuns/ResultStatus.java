package com.wkclz.common.emuns;


import com.wkclz.common.annotation.Desc;

/**
 * 返回状态
 */
@Desc("结果状态")
public enum ResultStatus {

    /**
     *
     */

    TOKEN_UNLL(10001, "token 为空！"),
    TOKEN_ERROR(10002, "token 不正确或已失效！"),
    TOKEN_ILLEGAL_TRANSFER(10003, "非法传输 token！"),
    TOKEN_ILLEGAL_LENGTH(10004, "非法长度的 token！"),
    TOKEN_SIGN_FAILD(10005, "token 签名效验失败！"),
    TOKEN_NOT_RIGHT(10006, "token 签发者不正确，请确认 token 来源！"),
    LOGIN_TIMEOUT(10007, "登录已失效，请重新登录！"),
    LOGIN_FORCE_TIMEOUT(10009, "登录时间过长，强制失效，请重新登录！"),
    APP_CODE_NULL(10101, "无法识别应用编码，请设置请求头或应用域名"),
    TENANT_NULL(10102, "无法识别租户编码，请设置请求头或租户域名"),

    CLIENT_CHANGE(20001, "用户登录环境改变！"),
    API_CORS(20002, "api url can not be cors"),
    ORIGIN_CORS(20003, "origin url can not be cors"),
    ERROR_ROUTER(20004, "err routers, check the uri!"),

    USERNAME_PASSWORD_ERROR(30001, "登录名或密码错误"),
    CAPTCHA_ERROR(30002, "图片验证码错误"),
    CAPTCHA_NEED(30003, "需要图片验证码"),
    MOBILE_CAPTCHA_ERROR(30004, "验证码错误"),
    EMAIL_CAPTCHA_ERROR(30004, "验证码错误"),

    UPDATE_NO_VERSION(40001, "操作需要带数据版本号！"),
    RECORD_NOT_EXIST_OR_OUT_OF_DATE(40002, "数据不存在或已不是最新的！"),
    RECORD_NOT_EXIST(40003, "数据不存在！"),
    PARAM_NO_ID(40004, "ID 不存在！"),
    PARAM_NULL(40005, "参数不存在！"),
    RECORD_DUPLICATE(40006, "数据重复，唯一性校验失败！"),

    NETWORK_ERROR(50001, "network error！"),
    NO_AVAILABLE_SERVER(50002, "no available server！"),
    UNKNOWN_RIBBON_ERROR(50003, "unknown ribbon error！"),

//    public static final String SYSTEM_ERROR = "System error, we have turned to the Admin of this website";
//    public static final String REDIS_IS_DISABLED = "Redis config or Redis server is error! no Redis will be support!";

    ORDER_TIMEOUT(60001, "订单支付超时已自动取消，请重新下单！"),
    ORDER_PAYD(60002, "订单已完成支付，请不要重复支付！"),
    ORDER_ERROR(60003, "订单状态异常，不能支付！"),
    ;

    private Integer code;
    private String msg;

    ResultStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
