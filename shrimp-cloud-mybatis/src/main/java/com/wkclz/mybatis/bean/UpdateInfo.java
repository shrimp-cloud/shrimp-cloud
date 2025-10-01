package com.wkclz.mybatis.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shrimp
 */
@Data
public class UpdateInfo implements Serializable {

    private String tableName;

    // INSERT, UPDATE, DELETE
    private String opType;

    private String script;

}
