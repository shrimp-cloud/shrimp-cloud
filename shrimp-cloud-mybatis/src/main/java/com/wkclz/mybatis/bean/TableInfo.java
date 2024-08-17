package com.wkclz.mybatis.bean;

import lombok.Data;

import java.util.List;

@Data
public class TableInfo {

    private String tableName;
    private String tableComment;
    private String dbType;

    private String engine;
    private Number autoIncrement;
    private String charset;
    private String collate;

    private List<ColumnInfo> columns;
    // index
    private List<KeyInfo> keys;

}
