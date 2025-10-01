package com.wkclz.mybatis.bean;

import lombok.Data;

@Data
public class ColumnInfo {

    private String tableSchema;
    private String tableName;

    private Boolean unsigned;
    private Boolean autoIncrement;
    private String columnName;
    private String dataType;
    private String charset;
    private String collate;
    private Long length;
    private String columnComment;
    private Object defaultValue;
    private Object onUpdate;
    private Boolean notNull;

}
