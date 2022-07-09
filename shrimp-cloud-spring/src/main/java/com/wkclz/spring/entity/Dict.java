package com.wkclz.spring.entity;

public class Dict {

    /**
     * 字典项
     */
    private String dictKey;

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 描述
     */
    private String description;


    public String getDictKey() {
        return dictKey;
    }

    public void setDictKey(String dictKey) {
        this.dictKey = dictKey;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

