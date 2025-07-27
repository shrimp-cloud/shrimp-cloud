package com.wkclz.camunda.feign;

import com.wkclz.camunda.config.CamundaConfig;
import com.wkclz.camunda.entity.repository.DefinitionDeploymentResponseEntity;
import com.wkclz.camunda.entity.repository.DefinitionEntity;
import com.wkclz.camunda.entity.repository.DefinitionQueryParams;
import com.wkclz.camunda.entity.repository.DefinitionXmlEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(
    name = "camundaRepositoryFeign",
    url = "${camunda.url:http://localhost:8080/engine-rest}",
    configuration = CamundaConfig.class
)
public interface CamundaRepositoryFeign {

    /**
     * 获取流程定义列表
     */
    @GetMapping("/process-definition")
    List<DefinitionEntity> processDefinitionList(@RequestParam DefinitionQueryParams params);

    /**
     * 获取流程定义详情
     */
    @GetMapping("/process-definition/{id}")
    DefinitionEntity processDefinition(@PathVariable("id") String id);

    /**
     * 获取流程定义XML
     */
    @GetMapping("/process-definition/{processDefinitionId}/xml")
    DefinitionXmlEntity processDefinitionXml(@PathVariable("processDefinitionId") String processDefinitionId);

    /**
     * 部署流程定义
     */
    @PostMapping(value = "/deployment/create", produces = {"application/json"}, consumes = {"multipart/form-data"})
    DefinitionDeploymentResponseEntity createDeployment(@RequestParam("deployment-name") String deploymentName,
                                                        @RequestParam("deployment-source") String deploymentSource,
                                                        @RequestParam("tenant-id") String tenantId,
                                                        @RequestParam("enable-duplicate-filtering") boolean enableDuplicateFiltering,
                                                        @RequestPart("data") MultipartFile[] bpmnXml);

    /**
     * 删除流程定义部署
     */
    @DeleteMapping("/deployment/{id}")
    void deleteDeployment(@PathVariable("id") String deploymentId, @RequestParam("cascade") boolean cascade);


}
