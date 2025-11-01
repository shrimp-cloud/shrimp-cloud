package com.wkclz.mybatis.dao;

import com.wkclz.mybatis.bean.ColumnInfo;
import com.wkclz.mybatis.bean.ColumnQuery;
import com.wkclz.mybatis.bean.IndexInfo;
import com.wkclz.mybatis.bean.TableInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shrimp
 */
@Mapper
public interface TableInfoMapper {



    List<TableInfo> getTables(TableInfo info);

    List<ColumnInfo> getColumns(TableInfo info);

    List<IndexInfo> getIndexs(TableInfo info);


    // 获取字段信息：附带字段出现的次数
    List<ColumnQuery> getColumnInfos4Options(ColumnQuery info);


    // 获取字段信息，包含字段，长度，备注
    List<ColumnInfo> getColumnLengthList(ColumnQuery info);


}
