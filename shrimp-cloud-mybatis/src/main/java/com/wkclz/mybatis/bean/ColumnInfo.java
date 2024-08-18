package com.wkclz.mybatis.bean;

import lombok.Data;

@Data
public class ColumnInfo {

    private Boolean unsigned;
    private Boolean autoIncrement;
    private String columnName;
    private String dataType;
    private String charset;
    private String collate;
    private Number length;
    private String comment;
    private Object defaultValue;
    private Object onUpdate;
    private Boolean notNull;

}
