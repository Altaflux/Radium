package com.kubadziworski.exception;

/**
 * Created by kuba on 04.05.16.
 */
public class ClassNotFoundForNameException extends RuntimeException {
    public ClassNotFoundForNameException(String className) {
        super("class not found " + className);
    }
}
