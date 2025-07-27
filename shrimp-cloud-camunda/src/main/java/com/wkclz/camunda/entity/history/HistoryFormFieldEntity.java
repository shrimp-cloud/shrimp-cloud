package com.wkclz.camunda.entity.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wkclz.camunda.config.MultiFormatDateDeserializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 历史表单字段实体
 */
@Data
public class HistoryFormFieldEntity implements Serializable {
    /**
     * 表单字段ID
     */
    private String id;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 字段ID
     */
    private String fieldId;
    /**
     * 字段值
     */
    private String fieldValue;
    /**
     * 创建时间
     */
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    private Date createTime;
}