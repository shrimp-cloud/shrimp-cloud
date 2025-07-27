package com.wkclz.camunda.entity.history;

import lombok.Data;

import java.io.Serializable;

/**
 * 历史用户操作日志实体
 * @author shrimp
 */
@Data
public class HistoryUserOperationLogEntity implements Serializable {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 部署ID
     */
    private String deploymentId;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程定义标识
     */
    private String processDefinitionKey;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 执行ID
     */
    private String executionId;
    /**
     * 案例定义ID
     */
    private String caseDefinitionId;
    /**
     * 案例实例ID
     */
    private String caseInstanceId;
    /**
     * 案例执行ID
     */
    private String caseExecutionId;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 操作类型
     */
    private String operationType;
    /**
     * 实体类型
     */
    private String entityType;
    /**
     * 属性
     */
    private String property;
    /**
     * 原始值
     */
    private String orgValue;
    /**
     * 新值
     */
    private String newValue;
}