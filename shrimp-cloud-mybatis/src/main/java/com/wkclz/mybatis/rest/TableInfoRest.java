package com.wkclz.mybatis.rest;


import com.wkclz.common.entity.Result;
import com.wkclz.mybatis.bean.ColumnQuery;
import com.wkclz.mybatis.bean.TableInfo;
import com.wkclz.mybatis.service.TableInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shrimp
 */
@RestController
public class TableInfoRest {


    @Resource
    private TableInfoService tableInfoService;


    @GetMapping(Routes.DB_TABLE_LIST)
    public Result dbTableList(TableInfo tableInfo) {
        List<TableInfo> tables = tableInfoService.getTables(tableInfo);
        return Result.data(tables);
    }


    @GetMapping(Routes.DB_COLUMN_LIST)
    public Result dbColumnList(ColumnQuery query) {
        List<ColumnQuery> columns = tableInfoService.getColumnInfos4Options(query);
        return Result.data(columns);
    }

}
