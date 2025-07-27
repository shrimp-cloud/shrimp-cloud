package com.wkclz.camunda.exception;

/**
 * @author shrimp
 */
public class CamundaException extends RuntimeException {

    public CamundaException() {
        super();
    }

    public CamundaException(String message) {
        super(message);
    }

    public static CamundaException error(String msg) {
        return new CamundaException(msg);
    }


}
