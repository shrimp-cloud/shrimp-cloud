package com.wkclz.camunda.service;

import com.wkclz.camunda.entity.repository.*;
import com.wkclz.camunda.exception.CamundaException;
import com.wkclz.camunda.feign.CamundaRepositoryFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

/**
 * @author shrimp
 */
@Service
public class CamundaRepositoryService {

    @Autowired
    private CamundaRepositoryFeign camundaRepositoryFeign;


    public List<DefinitionEntity> processDefinitionList(String deploymentId) {
        DefinitionQueryParams params = new DefinitionQueryParams();
        params.setDeploymentId(deploymentId);
        return camundaRepositoryFeign.processDefinitionList(params);
    }

    public List<DefinitionEntity> processDefinitionList(DefinitionQueryParams params) {
        return camundaRepositoryFeign.processDefinitionList(params);
    }


    /*
    public List<Definition> getDefinitionList(DefinitionVo definitionVo) {
        Map<String, Object> queryParams = new HashMap<>();
        if (StringUtils.isNotBlank(definitionVo.getName())) {
            queryParams.put("nameLike", definitionVo.getName());
        }
        if (StringUtils.isNotBlank(definitionVo.getDefinitionKey())) {
            queryParams.put("keyLike", definitionVo.getDefinitionKey());
        }
        queryParams.put("latestVersion", true);
        return (List) camundaRepositoryFeign.processDefinitionList(queryParams);
    }
    */

    public DefinitionEntity definitionDeploy(DefinitionDeployEntity entity) {
        String definitionKey = entity.getDefinitionKey();
        String definitionName = entity.getDefinitionName();
        String source = entity.getDefinitionSource();
        String tenantId = entity.getTenantId();
        String bpmnXml = entity.getBpmnXml();
        MultipartFile[] multipartFiles = convertStringToMultipart(bpmnXml, definitionKey + ".bpmn");
        DefinitionDeploymentResponseEntity deployment = camundaRepositoryFeign.createDeployment(
            definitionName, source, tenantId,false, multipartFiles
        );
        String deploymentId = deployment.getId();
        List<DefinitionEntity> definitions = processDefinitionList(deploymentId);

        definitions.sort(Comparator.comparingInt(DefinitionEntity::getVersion));
        return definitions.isEmpty() ? null : definitions.get(definitions.size() - 1);
    }

    public String readDefinitionXml(String definitionId) {
        DefinitionXmlEntity xml = camundaRepositoryFeign.processDefinitionXml(definitionId);
        return xml.getBpmn20Xml();
    }

    public void definitionDelete(String deploymentId) {
        if (deploymentId == null) {
            throw CamundaException.error("deploymentId 不能为空!");
        }
        // true 允许级联删除 ,不设置会导致数据库外键关联异常
        camundaRepositoryFeign.deleteDeployment(deploymentId, true);
    }

    private static MultipartFile[] convertStringToMultipart(String xmlContent, String fileName) {
        // 创建单个MultipartFile实例
        MultipartFile multipartFile = new MockMultipartFile(
            "data",
            fileName,
            "application/xml",
            xmlContent.getBytes(StandardCharsets.UTF_8)
        );
        // 返回包含单个MultipartFile的数组，或者根据需要调整大小
        return new MultipartFile[]{multipartFile};
    }
}
