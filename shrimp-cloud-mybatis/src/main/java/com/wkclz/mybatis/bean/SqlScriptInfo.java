package com.wkclz.mybatis.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shrimp
 */
@Data
public class SqlScriptInfo implements Serializable {

    // file 全名
    private String fileName;

    // ddl, dml
    private String type;

    // 表名
    private String tableName;

    // 脚本
    private String script;


    // 如果 DDL 是一个表
    private TableInfo tableInfo;;

    private UpdateInfo updateInfo;


}
