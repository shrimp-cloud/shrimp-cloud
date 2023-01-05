package com.wkclz.spring.rest;

import com.wkclz.common.entity.Result;
import com.wkclz.spring.entity.DictItem;
import com.wkclz.spring.helper.DictHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DictRest {

    @GetMapping(Routes.DICT_ITEMS)
    public Result dictItems(String type) {
        if (StringUtils.isBlank(type)) {
            return Result.error("type must not be null");
        }
        List<DictItem> dictItems = DictHelper.get(type);
        return Result.data(dictItems);
    }

}

