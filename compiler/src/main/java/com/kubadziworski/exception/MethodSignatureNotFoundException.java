package com.kubadziworski.exception;

import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;

import java.util.List;

/**
 * Created by kuba on 09.04.16.
 */
public class MethodSignatureNotFoundException extends RuntimeException {
    public MethodSignatureNotFoundException(Scope scope, String methodName, List<Argument> parameterTypes) {
        super("There is no method '" + methodName + "' with parameters " + parameterTypes + " for OwnerType: " + scope.getClassType());
    }

    public MethodSignatureNotFoundException(String methodName, List<Argument> parameterTypes, Type owner) {
        super("There is no method '" + methodName + "' with parameters " + parameterTypes + " for OwnerType: " + owner);
    }
}
