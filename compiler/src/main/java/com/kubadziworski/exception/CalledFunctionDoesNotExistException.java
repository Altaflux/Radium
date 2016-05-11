package com.kubadziworski.exception;

import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.expression.FunctionCall;

/**
 * Created by kuba on 02.04.16.
 */
public class CalledFunctionDoesNotExistException extends CompilationException {
    private final FunctionCall functionCall;

    public CalledFunctionDoesNotExistException(FunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    public CalledFunctionDoesNotExistException(FunctionCall functionCall, ReflectiveOperationException e) {
        this(functionCall);
    }

    public CalledFunctionDoesNotExistException(FunctionCall functionCall, Scope scope) {
        this(functionCall);
    }

    @Override
    public String getMessage() {
        return "Function call" + functionCall.toString() + "does not exists";
    }
}
