package com.wkclz.camunda.entity.repository;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程定义XML实体
 * @author shrimp
 */
@Data
public class DefinitionXmlEntity implements Serializable {

    /**
     * 流程定义ID
     */
    private String id;
    /**
     * BPMN 2.0 XML内容
     */
    private String bpmn20Xml;

}
