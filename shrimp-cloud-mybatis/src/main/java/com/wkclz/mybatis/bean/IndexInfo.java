package com.wkclz.mybatis.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IndexInfo implements Serializable {

    private String tableSchema;
    private String tableName;
    private String indexName;

    // Using btree/hash
    private String indexType;

    // ??
    private String type;


    // 临时字段，需要被合并到 columns 中
    private String columnName;

    private List<String> columns;

}
