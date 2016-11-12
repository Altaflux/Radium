package com.kubadziworski.exception;


public class UnreachableStatementException extends RuntimeException {

    public UnreachableStatementException(int line) {
        super("Unreachable statement at line " + line);
    }
}
