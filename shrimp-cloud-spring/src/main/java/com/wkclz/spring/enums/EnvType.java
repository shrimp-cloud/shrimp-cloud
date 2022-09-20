package com.wkclz.spring.enums;

import com.wkclz.common.annotation.Desc;

/**
 * 系统环境类型
 */
@Desc("系统环境")
public enum EnvType {

    /** 环境 */
    DEV("DEV", "开发环境"),
    SIT("SIT", "集成测试环境"),
    UAT("UAT", "验收测试环境"),
    PROD("PROD", "生产环境");

    private String key;
    private String value;

    EnvType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
