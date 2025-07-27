package com.wkclz.camunda.entity.repository;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Camunda 流程部署实体
 */
@Data
public class DefinitionDeploymentResponseEntity implements Serializable {

    /**
     * 部署ID
     */
    private String id;

    /**
     * 部署时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date deploymentTime;

    /**
     * 已部署的流程定义
     * key: processDefinitionId
     * value: DefinitionEntity
     */
    private Map<String, DefinitionEntity> deployedDefinitions;

    /**
     * 链接信息
     */
    private List<Link> links;

    /**
     * 链接信息实体
     */
    @Data
    public static class Link implements Serializable {
        /**
         * 请求方法
         */
        private String method;

        /**
         * 链接地址
         */
        private String href;

        /**
         * 关系
         */
        private String rel;
    }
}