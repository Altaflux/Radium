package com.kubadziworski.exception;

/**
 * Created by kuba on 05.05.16.
 */
public class FunctionNameEqualClassException extends RuntimeException {
    public FunctionNameEqualClassException(String functionName) {
        super("Function name cannot be same as the class : " + functionName);
    }
}
