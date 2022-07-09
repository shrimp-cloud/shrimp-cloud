package com.wkclz.spring.enums;

import com.wkclz.common.annotation.Desc;

/**
 * 系统环境类型
 */
@Desc("系统环境")
public enum EnvType {

    DEV("开发环境"),
    SIT("集成测试环境"),
    UAT("验收测试环境"),
    PROD("生产环境");

    private String value;

    EnvType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
