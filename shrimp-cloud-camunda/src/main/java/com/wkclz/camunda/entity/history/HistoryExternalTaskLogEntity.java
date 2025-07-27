package com.wkclz.camunda.entity.history;

import lombok.Data;

import java.io.Serializable;

/**
 * 历史外部任务日志实体
 * @author shrimp
 */
@Data
public class HistoryExternalTaskLogEntity implements Serializable {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 外部任务ID
     */
    private String externalTaskId;
    /**
     * 主题名称
     */
    private String topicName;
    /**
     * 工作者ID
     */
    private String workerId;
    /**
     * 重试次数
     */
    private String retries;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 活动实例ID
     */
    private String activityInstanceId;
    /**
     * 执行ID
     */
    private String executionId;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程定义标识
     */
    private String processDefinitionKey;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 状态
     */
    private String state;
}