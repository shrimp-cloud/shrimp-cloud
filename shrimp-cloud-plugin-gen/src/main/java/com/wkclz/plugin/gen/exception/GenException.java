package com.wkclz.plugin.gen.exception;

/**
 * @author shrimp
 */
public class GenException extends RuntimeException {

    public GenException() {
        super();
    }

    public GenException(String msg) {
        super(msg);
    }

    public static GenException error(String msg) {
        return new GenException(msg);
    }

}
