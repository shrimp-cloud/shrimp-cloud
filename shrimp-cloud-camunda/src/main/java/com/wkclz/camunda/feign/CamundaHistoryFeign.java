package com.wkclz.camunda.feign;

import com.wkclz.camunda.config.CamundaConfig;
import com.wkclz.camunda.entity.history.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author shrimp
 */
@FeignClient(
    name = "camundaHistoryFeign",
    url = "${camunda.url:http://localhost:8080/engine-rest}",
    configuration = CamundaConfig.class
)
public interface CamundaHistoryFeign {

    // 历史任务实例
    @GetMapping("/history/task")
    List<HistoryTaskInstanceEntity> historyTask(@RequestParam("processInstanceId") String processInstanceId);

    // 历史流程实例
    @GetMapping("/history/process-instance")
    List<HistoryProcessInstanceEntity> historyProcessInstance(@RequestParam("processDefinitionKey") String processDefinitionKey);

    // 历史活动实例
    @GetMapping("/history/activity-instance")
    List<HistoryActivityInstanceEntity> historyActivityInstance(@RequestParam("processInstanceId") String processInstanceId);

    // 历史变量实例
    @GetMapping("/history/variable-instance")
    List<HistoryVariableInstanceEntity> historyVariableInstance(@RequestParam("processInstanceId") String processInstanceId);

    // 历史表单属性
    @GetMapping("/history/form-field")
    List<HistoryFormFieldEntity> historyFormField(@RequestParam("processInstanceId") String processInstanceId);

    // 历史决策实例
    @GetMapping("/history/decision-instance")
    List<HistoryDecisionInstanceEntity> historyDecisionInstance(@RequestParam("processInstanceId") String processInstanceId);

    // 历史外部任务日志
    @GetMapping("/history/external-task-log")
    List<HistoryExternalTaskLogEntity> historyExternalTaskLog(@RequestParam("processInstanceId") String processInstanceId);

    // 历史用户操作日志
    @GetMapping("/history/user-operation-log")
    List<HistoryUserOperationLogEntity> historyUserOperationLog(@RequestParam("processInstanceId") String processInstanceId);

    // 历史身份链接
    @GetMapping("/history/identity-link-log")
    List<HistoryIdentityLinkLogEntity> historyIdentityLinkLog(@RequestParam("processInstanceId") String processInstanceId);
}
