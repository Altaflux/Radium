package com.kubadziworski.exception;


public class CompilationException extends RuntimeException {

    public CompilationException() {
    }

    public CompilationException(String message, Throwable cause) {
        super(message, cause);
    }
}
