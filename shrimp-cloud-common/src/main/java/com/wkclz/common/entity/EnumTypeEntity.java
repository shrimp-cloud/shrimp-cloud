package com.wkclz.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 枚举类型实体
 */
@Data
public class EnumTypeEntity implements Serializable {

    private Class clazz;
    private String enumType;
    private String enumTypeDesc;

}
