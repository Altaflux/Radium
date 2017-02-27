package com.kubadziworski.exception;

import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kuba on 09.04.16.
 */
public class MethodSignatureNotFoundException extends RuntimeException {

    public MethodSignatureNotFoundException(Scope scope, String methodName, List<ArgumentHolder> parameterTypes) {
        super("There is no method '" + methodName + "' with parameters " + parameterTypes.stream()
                .map(ArgumentHolder::getExpression).map(Expression::getType).map(Type::readableString).collect(Collectors.toList()) + " for OwnerType: " + scope.getClassType().readableString());
    }

    public MethodSignatureNotFoundException(String methodName, List<ArgumentHolder> parameterTypes, Type owner) {
        super("There is no method '" + methodName + "' with parameters " + parameterTypes.stream()
                .map(ArgumentHolder::getExpression).map(Expression::getType).map(Type::readableString).collect(Collectors.toList()) + " for OwnerType: " + owner.readableString());
    }
}
