package com.wkclz.spring.rest;

import com.wkclz.common.entity.Result;
import com.wkclz.spring.entity.DictItem;
import com.wkclz.spring.helper.DictHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DictRest {

    // 字典由 sys/mdm管理，此处只留下作为历史
    public Result dictItems(String type) {
        if (StringUtils.isBlank(type)) {
            return Result.error("type must not be null");
        }
        List<DictItem> dictItems = DictHelper.get(type);
        return Result.data(dictItems);
    }

}

