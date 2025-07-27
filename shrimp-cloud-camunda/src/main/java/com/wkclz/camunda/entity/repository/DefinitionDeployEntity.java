package com.wkclz.camunda.entity.repository;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shrimp
 */
@Data
public class DefinitionDeployEntity implements Serializable {

    private String definitionKey;
    private String definitionName;
    private String definitionSource;
    private String tenantId;
    private Boolean enableDuplicateFiltering;
    private String bpmnXml;

}
