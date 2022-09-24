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

    private String value;
    private String label;

    EnvType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
