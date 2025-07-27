package com.wkclz.camunda.entity.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史决策实例实体
 */
@Data
public class HistoryDecisionInstanceEntity implements Serializable {
    /**
     * 决策实例ID
     */
    private String id;
    /**
     * 决策定义ID
     */
    private String decisionDefinitionId;
    /**
     * 决策定义Key
     */
    private String decisionDefinitionKey;
    /**
     * 决策定义名称
     */
    private String decisionDefinitionName;
    /**
     * 评估时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date evaluationTime;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程定义Key
     */
    private String processDefinitionKey;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 案例定义ID
     */
    private String caseDefinitionId;
    /**
     * 案例定义Key
     */
    private String caseDefinitionKey;
    /**
     * 案例实例ID
     */
    private String caseInstanceId;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 活动实例ID
     */
    private String activityInstanceId;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 用户ID
     */
    private String userId;
}