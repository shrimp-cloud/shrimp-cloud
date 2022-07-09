package com.wkclz.spring.entity;

import java.util.List;

public class DictType {

    /**
     * 类型
     */
    private String dictType;

    /**
     * 类型描述信息
     */
    private String description;

    private List<Dict> dicts;


    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Dict> getDicts() {
        return dicts;
    }

    public void setDicts(List<Dict> dicts) {
        this.dicts = dicts;
    }
}

