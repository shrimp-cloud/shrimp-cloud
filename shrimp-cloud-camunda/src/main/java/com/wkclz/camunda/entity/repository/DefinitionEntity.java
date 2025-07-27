package com.wkclz.camunda.entity.repository;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程定义实体
 * @author shrimp
 */
@Data
public class DefinitionEntity implements Serializable {

    /**
     * 流程定义ID
     */
    private String id;

    /**
     * 流程定义key
     */
    private String key;

    /**
     * 流程定义分类
     */
    private String category;

    /**
     * 流程定义描述
     */
    private String description;

    /**
     * 流程定义名称
     */
    private String name;

    /**
     * 流程定义版本
     */
    private Integer version;

    /**
     * 资源名称
     */
    private String resource;

    /**
     * 部署ID
     */
    private String deploymentId;

    /**
     * 图形资源名称
     */
    private String diagram;

    /**
     * 是否已暂停
     */
    private Boolean suspended;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 版本标签
     */
    private String versionTag;

    /**
     * 历史生存时间
     */
    private Integer historyTimeToLive;

    /**
     * 是否可启动
     */
    private Boolean startableInTasklist;
}