package com.wkclz.camunda.entity.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史任务实例实体
 * @author shrimp
 */
@Data
public class HistoryTaskInstanceEntity implements Serializable {

    /**
     * 任务实例ID
     */
    private String id;
    /**
     * 流程定义标识
     */
    private String processDefinitionKey;
    /**
     * 流程定义ID
     */
    private String processDefinitionId;
    /**
     * 根流程实例ID
     */
    private String rootProcessInstanceId;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 执行实例ID
     */
    private String executionId;
    /**
     * 案例定义标识
     */
    private String caseDefinitionKey;
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
     * 活动实例ID
     */
    private String activityInstanceId;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 任务描述
     */
    private String description;
    /**
     * 删除原因
     */
    private String deleteReason;
    /**
     * 任务所有者
     */
    private String owner;
    /**
     * 任务受理人
     */
    private String assignee;
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
    /**
     * 任务定义标识
     */
    private String taskDefinitionKey;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 到期时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date dueDate;
    /**
     * 父任务ID
     */
    private String parentTaskId;
    /**
     * 跟进时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date followUpDate;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 移除时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date removalTime;
    /**
     * 任务状态
     */
    private String taskState;

    /**
     * 到期时间（字符串格式）
     */
    private String due;
    /**
     * 持续时间（字符串格式）
     */
    private String duration;
    /**
     * 跟进时间（字符串格式）
     */
    private String followUp;

}
