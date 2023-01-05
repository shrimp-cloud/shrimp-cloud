package com.wkclz.spring.entity;

public class DictItem {

    /**
     * 字典值
     */
    private String dictValue;

    /**
     * 字典标签
     */
    private String dictLabel;

    /**
     * EL 类型
     */
    private String elType;

    /**
     * 描述
     */
    private String description;


    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getDictLabel() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    public String getElType() {
        return elType;
    }

    public void setElType(String elType) {
        this.elType = elType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

