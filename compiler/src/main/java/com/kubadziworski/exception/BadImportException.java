package com.kubadziworski.exception;


public class BadImportException extends RuntimeException {

    public BadImportException(String packageImport){
        super("Invalid import for: " + packageImport);
    }
}
