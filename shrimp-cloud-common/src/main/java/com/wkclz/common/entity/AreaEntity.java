package com.wkclz.common.entity;

import java.io.Serializable;

/**
 * Description: Create by Shrimp Generator
 *
 * @author: wangkaicun @ current time
 */
public class AreaEntity implements Serializable {

    /**
     * 父区划代码
     */
    private Long parentAreaCode;

    /**
     * 统计用区划代码
     */
    private Long areaCode;

    /**
     * 城乡分类代码
     */
    private Integer typeCode;

    /**
     * 名称
     */
    private String name;

    /**
     * 级别（1-5）
     */
    private Integer level;

    /**
     * 是否叶子（1是，0 不是）
     */
    private Integer isLeaf;

    public Long getParentAreaCode() {
        return parentAreaCode;
    }

    public void setParentAreaCode(Long parentAreaCode) {
        this.parentAreaCode = parentAreaCode;
    }

    public Long getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Long areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(Integer typeCode) {
        this.typeCode = typeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(Integer isLeaf) {
        this.isLeaf = isLeaf;
    }
}
