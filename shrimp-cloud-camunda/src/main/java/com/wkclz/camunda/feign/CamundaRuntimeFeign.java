package com.wkclz.camunda.feign;

import com.wkclz.camunda.config.CamundaConfig;
import com.wkclz.camunda.entity.process.ProcessInstanceEntity;
import com.wkclz.camunda.entity.process.ProcessStartRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "camundaRuntimeFeign",
    url = "${camunda.url:http://localhost:8080/engine-rest}",
    configuration = CamundaConfig.class
)
public interface CamundaRuntimeFeign {

    /**
     * 启动流程实例
     */
    @PostMapping("/process-definition/{id}/start")
    ProcessInstanceEntity startProcessInsById(@PathVariable("id") String id, @RequestBody ProcessStartRequest request);

    /**
     * 获取流程实例信息
     */
    @PostMapping("/process-instance/{processInstanceId}")
    ProcessInstanceEntity getProcessIns(@PathVariable("processInstanceId") String processInstanceId);

}
