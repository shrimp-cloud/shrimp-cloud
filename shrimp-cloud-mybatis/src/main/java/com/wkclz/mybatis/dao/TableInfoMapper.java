package com.wkclz.mybatis.dao;

import com.wkclz.mybatis.bean.ColumnQuery;
import com.wkclz.mybatis.bean.TableInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shrimp
 */
@Mapper
public interface TableInfoMapper {


    List<TableInfo> getTables(TableInfo entity);

    // 获取字段信息：附带字段出现的次数
    List<ColumnQuery> getColumnInfos(ColumnQuery info);


}
