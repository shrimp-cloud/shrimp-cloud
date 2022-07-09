package com.wkclz.common.emuns;


import com.wkclz.common.annotation.Desc;

@Desc("时间范围类型")
public enum DateRangeType {

    HOUR("时"),
    YESTERDAY("天"),
    WEEK("周"),
    MONTH("月"),
    QUATER("季"),
    YEAR("年");

    private String value;

    DateRangeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
