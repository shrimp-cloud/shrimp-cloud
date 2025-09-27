package com.wkclz.spring.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RestInfo implements Serializable {

    private String appCode;
    private String apiCode;
    private String module;
    private String apiMethod;
    private String apiUri;
    private String apiName;
    private String apiDesc;
    private Integer writeFlag;

}
