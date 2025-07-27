package com.wkclz.camunda.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wkclz.camunda.entity.process.ProcessInstanceEntity;
import com.wkclz.camunda.entity.process.ProcessStartEntity;
import com.wkclz.camunda.entity.process.ProcessStartRequest;
import com.wkclz.camunda.exception.CamundaException;
import com.wkclz.camunda.feign.CamundaRuntimeFeign;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author shrimp
 */
@Service
public class CamundaRuntimeService {

    @Resource
    private ObjectMapper objectMapper;
    @Autowired
    private CamundaRuntimeFeign camundaRuntimeFeign;

    public ProcessInstanceEntity startProcess(ProcessStartEntity ins) {
        // 参数列举
        String definitionId = ins.getDefinitionId();
        String businessKey = ins.getBusinessKey();
        String flowVariables = ins.getFlowVariables();

        // 参数组装

        ProcessStartRequest request = new ProcessStartRequest();
        request.setBusinessKey(businessKey);
        request.addVariable("processInitiator", ProcessStartRequest.Type.String, ins.getProcessInitiator());

        // 定义参数
        if (flowVariables != null) {
            Map<String, Object> map;
            try {
                map = objectMapper.readValue(flowVariables, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw CamundaException.error("error json to Map: " + flowVariables);
            }
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                request.addVariable(entry.getKey(), ProcessStartRequest.Type.String, entry.getValue());
            }
        }
        // 发起流程
        return camundaRuntimeFeign.startProcessInsById(definitionId, request);
    }

}
