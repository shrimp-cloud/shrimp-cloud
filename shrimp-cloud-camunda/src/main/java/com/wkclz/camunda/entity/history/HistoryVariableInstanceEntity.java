package com.wkclz.camunda.entity.history;

import lombok.Data;

import java.io.Serializable;

/**
 * 历史变量实例实体
 * @author shrimp
 */
@Data
public class HistoryVariableInstanceEntity implements Serializable {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 变量名称
     */
    private String variableName;
    /**
     * 变量类型
     */
    private String variableType;
    /**
     * 变量值
     */
    private Object value;
    /**
     * 流程定义标识
     */
    private String processDefinitionKey;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 执行ID
     */
    private String executionId;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 活动实例ID
     */
    private String activityInstanceId;
}