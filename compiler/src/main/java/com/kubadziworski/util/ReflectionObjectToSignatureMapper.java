package com.kubadziworski.util;

import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by kuba on 03.05.16.
 */
public final class ReflectionObjectToSignatureMapper {

    public static FunctionSignature fromMethod(Method method) {
        String name = method.getName();
        List<Parameter> parameters = Arrays.stream(method.getParameters())
                .map(p -> new Parameter(p.getName(), TypeResolver.getFromTypeName(p.getType().getCanonicalName()), null))
                .collect(toList());
        Class<?> returnType = method.getReturnType();
        Type owner = ClassTypeFactory.createClassType(method.getDeclaringClass().getName());
        return new FunctionSignature(name, parameters, TypeResolver.getFromTypeName(returnType.getCanonicalName()), method.getModifiers(), owner);
    }

    public static FunctionSignature fromConstructor(Constructor constructor, Type owner) {
        String name = constructor.getName();
        List<Parameter> parameters = Arrays.stream(constructor.getParameters())
                .map(p -> new Parameter(p.getName(), TypeResolver.getFromTypeName(p.getType().getCanonicalName()), null))
                .collect(toList());

        return new FunctionSignature(name, parameters, UnitType.INSTANCE, constructor.getModifiers(), owner);
    }

    public static Field fromField(java.lang.reflect.Field field, Type owner) {
        String name = field.getName();
        return new Field(name, owner, TypeResolver.getFromTypeName(field.getType().getCanonicalName()), field.getModifiers());
    }
}
