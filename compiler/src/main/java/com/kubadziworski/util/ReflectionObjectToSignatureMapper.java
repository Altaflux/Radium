package com.kubadziworski.util;

import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import radium.annotations.NotNull;
import radium.annotations.Nullable;

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
                .map(p -> new Parameter(p.getName(), TypeResolver.getTypeFromNameWithClazzAlias(p.getType().getCanonicalName(), getNullability(p)), null))
                .collect(toList());
        Class<?> returnType = method.getReturnType();
        Type owner = ClassTypeFactory.createClassType(method.getDeclaringClass().getName());
        return new FunctionSignature(name, parameters, TypeResolver.getTypeFromNameWithClazzAlias(returnType.getCanonicalName(), getNullability(returnType)), method.getModifiers(), owner);
    }

    public static FunctionSignature fromConstructor(Constructor constructor, Type owner) {
        String name = constructor.getName();
        List<Parameter> parameters = Arrays.stream(constructor.getParameters())
                .map(p -> new Parameter(p.getName(), TypeResolver.getTypeFromNameWithClazzAlias(p.getType().getCanonicalName(), getNullability(p)), null))
                .collect(toList());

        return new FunctionSignature(name, parameters, UnitType.INSTANCE, constructor.getModifiers(), owner);
    }

    public static Field fromField(java.lang.reflect.Field field, Type owner) {
        String name = field.getName();
        return new Field(name, owner, TypeResolver.getTypeFromNameWithClazzAlias(field.getType().getCanonicalName(), getNullability(field)), field.getModifiers());
    }

    private static Type.Nullability getNullability(java.lang.reflect.Field field) {
        if(field.getType().isPrimitive()){
            return Type.Nullability.NOT_NULL;
        }

        boolean notNull = Arrays.stream(field.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(NotNull.class)).findAny().isPresent();

        boolean nullable = Arrays.stream(field.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(Nullable.class)).findAny().isPresent();

        return getNullability(notNull, nullable);
    }

    private static Type.Nullability getNullability(java.lang.reflect.Parameter parameter) {
        if(parameter.getType().isPrimitive()){
            return Type.Nullability.NOT_NULL;
        }

        boolean notNull = Arrays.stream(parameter.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(NotNull.class)).findAny().isPresent();

        boolean nullable = Arrays.stream(parameter.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(Nullable.class)).findAny().isPresent();

        return getNullability(notNull, nullable);
    }

    private static Type.Nullability getNullability(Class clazz) {
        if(clazz.isPrimitive()){
            return Type.Nullability.NOT_NULL;
        }

        boolean notNull = Arrays.stream(clazz.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(NotNull.class)).findAny().isPresent();

        boolean nullable = Arrays.stream(clazz.getAnnotations())
                .filter(annotation -> annotation.annotationType().equals(Nullable.class)).findAny().isPresent();

        return getNullability(notNull, nullable);
    }

    private static Type.Nullability getNullability(boolean notNull, boolean nullable) {
        if (notNull) {
            return Type.Nullability.NOT_NULL;
        }
        if (nullable) {
            return Type.Nullability.NULLABLE;
        }
        return Type.Nullability.UNKNOWN;
    }
}
