package com.wkclz.camunda.entity.history;

import lombok.Data;

import java.io.Serializable;

/**
 * 历史身份链接日志实体
 * @author shrimp
 */
@Data
public class HistoryIdentityLinkLogEntity implements Serializable {
    /**
     * 主键ID
     */
    private String id;
    /**
     * 时间
     */
    private String time;
    /**
     * 类型
     */
    private String type;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 组ID
     */
    private String groupId;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 流程定义标识
     */
    private String processDefinitionKey;
    /**
     * 操作类型
     */
    private String operationType;
    /**
     * 分配者ID
     */
    private String assignerId;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 移除时间
     */
    private String removalTime;
    /**
     * 根流程实例ID
     */
    private String rootProcessInstanceId;
}