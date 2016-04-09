package com.kubadziworski.antlr.exceptions;

import com.kubadziworski.antlr.domain.scope.Scope;

/**
 * Created by kuba on 09.04.16.
 */
public class MethodSignatureNotFoundException extends RuntimeException {
    public MethodSignatureNotFoundException(Scope scope, String methodName) {
        super("There is no method " + methodName);
    }
}
