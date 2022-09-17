package com.wkclz.mybatis.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkclz.common.entity.BaseEntity;

import java.util.List;
import java.util.function.Function;

public class PageQuery<T extends BaseEntity> {

    private T param;

    public PageQuery(T param){
        param.init();
        this.param = param;
        PageHelper.startPage(param.getCurrent().intValue(), param.getSize().intValue());
    }
    public PageData page(List list){
        Page listPage = (Page) list;
        PageData<T> pageData = new PageData<>(param);
        pageData.setTotal(listPage.getTotal());
        pageData.setRows(listPage.getResult());
        return pageData;
    }

    public static <T extends BaseEntity> PageQuery param(T param) {
        return new PageQuery<>(param);
    }

    public PageData page(Function<T, List> function){
        Page listPage = (Page)function.apply(param);
        PageData<T> pageData = new PageData<>(param);
        pageData.setTotal(listPage.getTotal());
        pageData.setRows(listPage.getResult());
        return pageData;
    }

    public static <T extends BaseEntity> PageData page(T param, Function<T, List> function){
        param.init();
        PageHelper.startPage(param.getCurrent().intValue(), param.getSize().intValue());

        Page listPage = (Page)function.apply(param);
        PageData<T> pageData = new PageData<>(param);
        pageData.setTotal(listPage.getTotal());
        pageData.setRows(listPage.getResult());
        return pageData;
    }

}