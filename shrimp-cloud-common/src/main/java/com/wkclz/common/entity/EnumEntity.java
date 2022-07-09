package com.wkclz.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 枚举内容
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumEntity {

    private String enumType;
    private String enumTypeDesc;
    private String enumKey;
    private String enumValue;

    public String getEnumType() {
        return enumType;
    }

    public void setEnumType(String enumType) {
        this.enumType = enumType;
    }

    public String getEnumTypeDesc() {
        return enumTypeDesc;
    }

    public void setEnumTypeDesc(String enumTypeDesc) {
        this.enumTypeDesc = enumTypeDesc;
    }

    public String getEnumKey() {
        return enumKey;
    }

    public void setEnumKey(String enumKey) {
        this.enumKey = enumKey;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }
}
