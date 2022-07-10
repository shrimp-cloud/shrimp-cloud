package com.wkclz.mybatis.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkclz.common.entity.BaseEntity;

import java.util.List;

public class PageHandle<T extends BaseEntity> {

    private T entity;

    public PageHandle(T entity){
        entity.init();
        this.entity = entity;
        PageHelper.startPage(entity.getPageNo(), entity.getPageSize());
    }

    public PageData page(List list){
        Page listPage = (Page) list;
        long total = listPage.getTotal();
        PageData<T> pageData = new PageData<>(entity);
        pageData.setTotalCount(Long.valueOf(total).intValue());
        pageData.setRows(list);
        return pageData;
    }

}
