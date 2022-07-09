package com.wkclz.common.entity;

/**
 * 枚举类型实体
 */
public class EnumTypeEntity {

    private Class clazz;
    private String enumType;
    private String enumTypeDesc;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

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
}
