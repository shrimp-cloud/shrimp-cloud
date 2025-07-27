package com.wkclz.camunda.feign;

import com.wkclz.camunda.config.CamundaConfig;
import com.wkclz.camunda.entity.TaskEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author shrimp
 */
@FeignClient(
    name = "camundaTaskFeign",
    url = "${camunda.url:http://localhost:8080/engine-rest}",
    configuration = CamundaConfig.class
)
public interface CamundaTaskFeign {

    /**
     * 获取指定用户的任务列表
     */
    @GetMapping("/task")
    List<TaskEntity> task(@RequestParam("assignee") String assignee);

    /**
     * 完成任务
     */
    @PostMapping("/task/{taskInstanceId}/complete")
    Object taskComplete(@PathVariable("taskInstanceId") String taskInstanceId, @RequestBody Map<String, Object> requestBody);

    /**
     * 根据任务ID获取任务详情
     */
    @GetMapping("/task/{taskId}")
    TaskEntity getTask(@PathVariable("taskId") String taskId);

    /**
     * 认领任务
     */
    @PostMapping("/task/{taskId}/claim")
    void claimTask(@PathVariable("taskId") String taskId, @RequestParam("userId") String username);

    /**
     * 取消认领任务
     */
    @PostMapping("/task/{taskId}/unclaim")
    void unclaimTask(@PathVariable("taskId") String taskId);

    /**
     * 委派任务
     */
    @PostMapping("/task/{taskId}/delegate")
    void delegateTask(@PathVariable("taskId") String taskId, @RequestParam("userId") String username);

    /**
     * 转交任务
     */
    @PostMapping("/task/{taskId}/assignee")
    void setAssignee(@PathVariable("taskId") String taskId, @RequestParam("userId") String username);

    /**
     * 获取任务表单变量
     */
    @GetMapping("/task/{taskId}/form-variables")
    Map<String, Object> getTaskFormVariables(@PathVariable("taskId") String taskId);

    /**
     * 设置任务优先级
     */
    @PutMapping("/task/{taskId}/priority")
    void setTaskPriority(@PathVariable("taskId") String taskId, @RequestParam("priority") Integer priority);

    /**
     * 添加任务评论
     */
    @PostMapping("/task/{taskId}/comment/create")
    void createTaskComment(@PathVariable("taskId") String taskId, @RequestParam("message") String message);

    /**
     * 撤回流程
     */
    @PostMapping("/process-instance/{processInstanceId}/revoke")
    void revokeProcess(@PathVariable("processInstanceId") String processInstanceId);

    /**
     * 添加流程评论
     */
    @PostMapping("/process-instance/{processInstanceId}/comment/create")
    void createProcessComment(@PathVariable("processInstanceId") String processInstanceId, @RequestParam("message") String message);

    /**
     * 加签任务
     */
    @PostMapping("/task/{taskId}/add-sign")
    void addSign(@PathVariable("taskId") String taskId, @RequestParam("userId") String assignee);
}
