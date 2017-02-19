package com.kubadziworski.exception;


public class SyntaxException extends RuntimeException {

    public SyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
