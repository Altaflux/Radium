package com.kubadziworski.exception;

/**
 * Created by plozano on 11/2/2016.
 */
public class FinalFieldModificationException extends RuntimeException {

    public FinalFieldModificationException(String message) {
        super(message);
    }
}
