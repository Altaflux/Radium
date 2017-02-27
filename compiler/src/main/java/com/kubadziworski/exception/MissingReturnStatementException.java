package com.kubadziworski.exception;

import com.kubadziworski.domain.scope.FunctionSignature;


public class MissingReturnStatementException extends RuntimeException {

    public MissingReturnStatementException(FunctionSignature signature) {
        super("No return specified for method: \"" + signature.getName() + "\" with return type: " + signature.getReturnType().readableString());
    }
}
