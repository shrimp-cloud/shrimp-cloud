package com.wkclz.common.entity;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class FieldInfo {

    private String fileName;
    private Class<?> fileClass;
    private Method getter;

}
