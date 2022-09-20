package com.wkclz.spring.rest;

import com.wkclz.common.entity.Result;
import com.wkclz.spring.entity.EnumDict;
import com.wkclz.spring.helper.EnumDictHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EnumDictRest {

    @GetMapping(Routes.ENUMS_DICT)
    public Result get(String type) {
        if (StringUtils.isBlank(type)) {
            return Result.error("type must not be null");
        }
        List<EnumDict> enumDicts = EnumDictHelper.get(type);
        return Result.data(enumDicts);
    }
}

