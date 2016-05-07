package com.kubadziworski.util;

import com.kubadziworski.domain.expression.FunctionParameter;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.ClassType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Created by kuba on 03.05.16.
 */
public class ReflectionObjectToSignatureMapper {
    public static FunctionSignature fromMethod(Method method) {
        String name = method.getName();
        List<FunctionParameter> parameters = Arrays.stream(method.getParameters())
                .map(p -> new FunctionParameter(p.getName(), new ClassType(p.getType().getCanonicalName()), Optional.empty()))
                .collect(toList());
        Class<?> returnType = method.getReturnType();
        return new FunctionSignature(name, parameters, new ClassType(returnType.getCanonicalName()));
    }

    public static FunctionSignature fromConstructor(Constructor constructor) {
        String name = constructor.getName();
        List<FunctionParameter> parameters = Arrays.stream(constructor.getParameters())
                .map(p -> new FunctionParameter(p.getName(), new ClassType(p.getType().getCanonicalName()), Optional.empty()))
                .collect(toList());
        Class<?> returnType = constructor.getDeclaringClass();
        return new FunctionSignature(name, parameters, new ClassType(returnType.getCanonicalName()));
    }
}
