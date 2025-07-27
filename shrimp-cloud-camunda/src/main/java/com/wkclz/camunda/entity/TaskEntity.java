package com.wkclz.camunda.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskEntity implements Serializable {

    /**
     * 任务的唯一标识符，由 Camunda 引擎自动生成
     */
    private String id;

    /**
     * 任务的显示名称，通常在流程定义中指定
     */
    private String name;

    /**
     * 任务的处理人，指定执行该任务的具体用户
     */
    private String assignee;

    /**
     * 任务的创建时间，由 Camunda 引擎自动设置
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date created;

    /**
     * 任务的详细描述，用于提供额外的任务相关信息
     */
    private String description;

    /**
     * 任务关联的执行实例ID，用于标识任务在流程执行过程中的具体位置
     */
    private String executionId;

    /**
     * 任务的优先级，数值越大优先级越高，用于任务队列的排序
     */
    private Integer priority;

    /**
     * 任务所属的流程定义ID，标识任务属于哪个流程定义
     */
    private String processDefinitionId;

    /**
     * 任务所属的流程实例ID，标识任务属于哪个正在运行的流程实例
     */
    private String processInstanceId;

    /**
     * 任务定义的唯一标识符，用于在流程定义中识别特定的任务节点
     */
    private String taskDefinitionKey;

    /**
     * 标识任务是否被挂起，true表示任务被挂起无法执行，false表示任务可以正常执行
     */
    private Boolean suspended;

}
