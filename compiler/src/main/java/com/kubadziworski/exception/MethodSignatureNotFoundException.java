package com.kubadziworski.exception;

import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;

import java.util.List;

/**
 * Created by kuba on 09.04.16.
 */
public class MethodSignatureNotFoundException extends RuntimeException {
    public MethodSignatureNotFoundException(Scope scope, String methodName, List<Type> parameterTypes) {
        super("There is no method " + methodName + " with parameters " + parameterTypes);
    }
}
