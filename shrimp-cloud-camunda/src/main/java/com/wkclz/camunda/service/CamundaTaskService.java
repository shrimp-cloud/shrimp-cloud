package com.wkclz.camunda.service;

import com.wkclz.camunda.entity.TaskEntity;
import com.wkclz.camunda.feign.CamundaTaskFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CamundaTaskService {

    @Autowired
    private CamundaTaskFeign camundaTaskFeign;

    public List<TaskEntity> getAssigneeTask(String assignee) {
        List<TaskEntity> task = camundaTaskFeign.task(assignee);
        return task;
    }

    public void completeTask(String taskInstanceId, Integer approved, String comment) {
        // 构建请求体（可以包含流程变量）
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("approved", approved);
        requestBody.put("comment", comment);

        camundaTaskFeign.taskComplete(taskInstanceId, requestBody);
    }

    public void transferTask(String taskInstanceId, String assignee, String comment) {
        // 使用 setAssignee 方法转交任务
        camundaTaskFeign.setAssignee(taskInstanceId, assignee);
        // 添加评论
        if (comment != null && !comment.isEmpty()) {
            camundaTaskFeign.createTaskComment(taskInstanceId, comment);
        }
    }

    public void delegateTask(String taskInstanceId, String assignee, String comment) {
        // 使用 delegateTask 方法委派任务
        camundaTaskFeign.delegateTask(taskInstanceId, assignee);
        // 添加评论
        if (comment != null && !comment.isEmpty()) {
            camundaTaskFeign.createTaskComment(taskInstanceId, comment);
        }
    }

    public void addSign(String taskInstanceId, String assignee, String comment) {
        // 使用 addSign 方法进行加签操作
        camundaTaskFeign.addSign(taskInstanceId, assignee);
        // 添加评论
        if (comment != null && !comment.isEmpty()) {
            camundaTaskFeign.createTaskComment(taskInstanceId, comment);
        }
    }

    public void revokeProcess(String processInstanceId, String comment) {
        // 使用 revokeProcess 方法撤回流程
        camundaTaskFeign.revokeProcess(processInstanceId);
        // 添加评论
        if (comment != null && !comment.isEmpty()) {
            camundaTaskFeign.createProcessComment(processInstanceId, comment);
        }
    }

}

