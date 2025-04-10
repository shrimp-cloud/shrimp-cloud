package com.wkclz.spring.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class DebugInfo implements Serializable {

    private String debugId;

    private String info;

    private Long startTime;

    private Long upperTime;

    private Long currentTime;

    private Long endTime;

    private int step;

}
