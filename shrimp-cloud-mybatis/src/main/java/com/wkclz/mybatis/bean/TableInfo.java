package com.wkclz.mybatis.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TableInfo implements Serializable {

    private String tableSchema;

    private String tableName;
    private String tableComment;
    private String dbType;

    private String engine;
    private Long autoIncrement;
    private String charset;
    private String collate;



    private List<ColumnInfo> columns;
    // index
    private List<IndexInfo> indexs;

    // 辅助查询字段
    private List<String> tableNames;

}
