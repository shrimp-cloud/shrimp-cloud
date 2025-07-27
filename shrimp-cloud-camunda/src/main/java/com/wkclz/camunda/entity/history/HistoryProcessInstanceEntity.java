package com.wkclz.camunda.entity.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 历史流程实例实体
 * @author shrimp
 */
@Data
public class HistoryProcessInstanceEntity implements Serializable {
    /**
     * 主键ID
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
     * 流程定义名称
     */
    private String processDefinitionName;
    /**
     * 流程定义版本
     */
    private String processDefinitionVersion;
    /**
     * 开始时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private String startTime;
    /**
     * 结束时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private String endTime;
    /**
     * 持续时间（毫秒）
     */
    private String durationInMillis;
    /**
     * 启动用户ID
     */
    private String startUserId;
    /**
     * 开始活动ID
     */
    private String startActivityId;
    /**
     * 删除原因
     */
    private String deleteReason;
    /**
     * 父流程实例ID
     */
    private String superProcessInstanceId;
    /**
     * 状态
     */
    private String state;
}