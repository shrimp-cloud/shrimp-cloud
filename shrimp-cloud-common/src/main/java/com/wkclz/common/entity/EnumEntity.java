package com.wkclz.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 枚举内容
 */
@Data
public class EnumEntity implements Serializable {

    private String enumType;
    private String enumTypeDesc;
    private String enumKey;
    private String enumValue;

}
