package com.wkclz.camunda.entity.process;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程实例实体
 * @author shrimp
 */
@Data
public class ProcessInstanceEntity implements Serializable {
    /**
     * 流程实例ID
     */
    private String id;

    /**
     * 流程定义ID
     */
    private String processDefinitionId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 流程定义名称
     */
    private String processDefinitionName;

    /**
     * 流程定义版本
     */
    private Integer processDefinitionVersion;

    /**
     * 业务键值
     */
    private String businessKey;

    /**
     * 父流程实例ID
     */
    private String parentId;

    /**
     * 根流程实例ID
     */
    private String rootProcessInstanceId;

    /**
     * 案例实例ID
     */
    private String caseInstanceId;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 状态
     */
    private String state;

    /**
     * 是否挂起
     */
    private Boolean suspended;

    /**
     * 开始时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date endTime;

    /**
     * 持续时间（毫秒）
     */
    private Long durationInMillis;
}