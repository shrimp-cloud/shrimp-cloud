package com.wkclz.common.emuns;


import com.wkclz.common.annotation.Desc;

/**
 * @author: wangkaicun @ 2019-05-04 14:32:01
 * @Descript: 系统配置默认值，仅用于系统初始化【系统启动后的真实值，使用系统真实值】
 */
@Desc("系统配置默认值")
public enum SysConfigEnum {

    /**
     *
     */

    LOGIN_TIMES("login_times", "0", "系统登录次数"),
    SYSTEM_KEY("system_key", "system_key", "系统key"),
    PROTOCOL("protocol", "http", "【系统】协议"),
    OSS_INNER_ENDPOINT("oss_inner_endpoint", "http://dev.oss.wkclz.com", "【OSS】内网-endpoint"),
    OSS_OUTER_ENDPOINT("oss_outer_endpoint", "http://dev.oss.wkclz.com", "【OSS】外网-endpoint"),
    OSS_ACCESS_KEY_ID("oss_access_key_id", "oss_access_key_id", "【OSS】AccessKeyId"),
    OSS_ACCESS_KEY_SECRET("oss_access_key_secret", "oss_access_key_secret", "【OSS】AccessKeySecret"),
    OSS_BUCKET_NAME("oss_bucket_name", "com-wkclz-dev", "【OSS】BucketName"),
    CORS_API("cors_api", "localhost,127.0.0.1,api.wkclz.com", "【跨域】后端接口地址（不需要带端口）"),
    CORS_FRONT("cors_front", "localhost,127.0.0.1,www.wkclz.com", "【跨域】前端页面地址（不需要带端口）"),
    COOKIE_DOMAIN("cookie_domain", "wkclz.com", "Cookie 作用域名"),
    PLATFORM_DOMAIN("platform_domain", "wkclz.com", "平台域名"),
    ;

    private String key;
    private String value;
    private String remark;

    SysConfigEnum(String key, String value, String remark) {
        this.key = key;
        this.value = value;
        this.remark = remark;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }


}
