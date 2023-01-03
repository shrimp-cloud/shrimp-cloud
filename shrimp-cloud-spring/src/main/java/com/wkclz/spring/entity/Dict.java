package com.wkclz.spring.entity;

import java.util.List;

public class Dict {

    /**
     * 类型
     */
    private String dictType;

    /**
     * 类型描述信息
     */
    private String description;

    private List<DictItem> items;


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

    public List<DictItem> getItems() {
        return items;
    }

    public void setItems(List<DictItem> items) {
        this.items = items;
    }
}

