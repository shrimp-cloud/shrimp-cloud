package com.wkclz.mybatis.rest;


import com.wkclz.common.annotation.Desc;
import com.wkclz.common.annotation.Router;

/**
 * @author shrimp
 */
@Router("mdm")
public interface Routes {


    /**
     * 注意权限！！！，内容极其敏感
     */



    @Desc("1. 数据库-表")
    String DB_TABLE_LIST = "/db/table/list";
    @Desc("2. 数据库-字段")
    String DB_COLUMN_LIST = "/db/column/list";

}
