package com.wkclz.camunda.entity.repository;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shrimp
 */
@Data
public class DefinitionQueryParams implements Serializable {
    private String versionTag;
    private String name;
    private String nameLike;
    private String deploymentId;
    private String key;
    private String keyLike;
    private String category;
    private String categoryLike;
    private String resourceName;
    private String resourceNameLike;
    private String tenantIdIn;
    private Boolean withoutTenantId;
    private Boolean includeProcessDefinitionsWithoutTenantId;
    private String sortBy;
    private String sortOrder;
    private Integer firstResult;
    private Integer maxResults;

}