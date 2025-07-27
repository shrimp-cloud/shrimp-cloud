package com.wkclz.camunda.service;

import com.wkclz.camunda.entity.history.HistoryTaskInstanceEntity;
import com.wkclz.camunda.feign.CamundaHistoryFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CamundaHistoryService {

    @Autowired
    private CamundaHistoryFeign camundaHistoryFeign;

    public List<HistoryTaskInstanceEntity> getHistory(String processInstanceId) {
        return camundaHistoryFeign.historyTask(processInstanceId);
    }



}
