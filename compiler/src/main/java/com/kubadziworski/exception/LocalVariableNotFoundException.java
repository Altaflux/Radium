package com.kubadziworski.exception;

import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.Scope;

public class LocalVariableNotFoundException extends RuntimeException {
    public LocalVariableNotFoundException(Scope scope, String variableName) {
        super("No local varaible found for name \"" + variableName + "\" found in scope" + scope);
    }

    public LocalVariableNotFoundException(FunctionScope scope, String variableName) {
        super("No local varaible found for name \"" + variableName + "\" found in scope" + scope);
    }


}
