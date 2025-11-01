package com.wkclz.mybatis.service;

import com.wkclz.mybatis.bean.ColumnQuery;
import com.wkclz.mybatis.bean.TableInfo;
import com.wkclz.mybatis.config.ShrimpMyBatisConfig;
import com.wkclz.mybatis.dao.TableInfoMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author shrimp
 */
@Service
public class TableInfoService {

    @Resource
    private ShrimpMyBatisConfig config;
    @Resource
    private TableInfoMapper tableInfoMapper;


    public List<TableInfo> getTables(TableInfo entity) {
        if (entity == null) {
            entity = new TableInfo();
        }
        entity.setTableSchema(config.getTableSchema());
        return tableInfoMapper.getTables(entity);
    }

    public List<ColumnQuery> getColumnInfos4Options(ColumnQuery query) {
        if (query == null) {
            return Collections.emptyList();
        }
        query.setTableSchema(config.getTableSchema());
        return tableInfoMapper.getColumnInfos4Options(query);
    }

}
