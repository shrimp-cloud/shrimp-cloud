package com.wkclz.spring.entity;

import java.util.List;

public class RestInfo {

    private String method;

    private String uri;

    private String name;

    private String path;

    private String desc;

    private String module;


    private Class<?> returnType;

    private List<Class<?>> parameterTypes;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public List<Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(List<Class<?>> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
