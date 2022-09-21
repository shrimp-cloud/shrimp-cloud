package com.wkclz.spring.entity;

public class RestInfo {

    private String method;
    private String uri;
    private String name;
    private String desc;
    /*
    private Class<?> returnType;
    private List<Class<?>> parameterTypes;
    */

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
