package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;

import java.util.List;
import java.util.Optional;


public class EnkelScope {

    private final GlobalScope globalScope;


    public EnkelScope(GlobalScope globalScope) {
        this.globalScope = globalScope;
    }

    public Optional<FunctionSignature> getMethodSignature(Type owner, String methodName, List<Argument> arguments) {
        try {
            return Optional.of(globalScope.scopeMap.get(owner.getName()).getMethodCallSignature(methodName, arguments));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<com.kubadziworski.domain.scope.Field> getFieldSignature(Type owner, String fieldName) {
        try {
            return Optional.of(globalScope.scopeMap.get(owner.getName()).getField(fieldName));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<FunctionSignature> getConstructorSignature(ClassType className, List<Argument> arguments) {
        try {
            return Optional.of(globalScope.scopeMap.get(className.getName()).getConstructorCallSignature(className.getName(), arguments));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}