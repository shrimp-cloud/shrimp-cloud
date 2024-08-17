package com.wkclz.mybatis.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KeyInfo implements Serializable {

    private String name;
    private String indexType;
    private String type;

    private List<ColumnInfo> columns;

}
