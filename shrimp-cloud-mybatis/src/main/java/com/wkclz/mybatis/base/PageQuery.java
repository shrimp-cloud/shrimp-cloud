package com.wkclz.mybatis.base;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wkclz.common.entity.BaseEntity;

import java.util.List;
import java.util.function.Function;

public class PageQuery {

    public static <T extends BaseEntity> PageData page(T param, Function<T, List> function) {
        try {
            param.init();
            int current = param.getCurrent().intValue();
            int size = param.getSize().intValue();
            PageHelper.startPage(current, size);

            Page listPage = (Page)function.apply(param);
            PageData<T> pageData = new PageData<>(param);
            pageData.setTotal(listPage.getTotal());
            pageData.setRows(listPage.getResult());
            return pageData;
        } finally {
            PageHelper.clearPage();
        }
    }

}