package com.wkclz.camunda.entity.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史活动实例实体
 */
@Data
public class HistoryActivityInstanceEntity implements Serializable {
    /**
     * 活动实例ID
     */
    private String id;
    /**
     * 活动ID
     */
    private String activityId;
    /**
     * 活动名称
     */
    private String activityName;
    /**
     * 活动类型
     */
    private String activityType;
    /**
     * 流程定义Key
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
     * 执行实例ID
     */
    private String executionId;
    /**
     * 任务ID
     */
    private String taskId;
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