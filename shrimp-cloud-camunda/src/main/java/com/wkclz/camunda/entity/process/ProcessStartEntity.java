package com.wkclz.camunda.entity.process;

import lombok.Data;

import java.io.Serializable;

/**
 * @author shrimp
 */
@Data
public class ProcessStartEntity implements Serializable {

    private String definitionId;
    private String businessKey;
    private String flowVariables;
    private String processInitiator;

}
