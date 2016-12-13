package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Created by kuba on 11.05.16.
 */
public class ClassPathScope {

    public Optional<FunctionSignature> getMethodSignature(JavaClassType owner, String methodName, List<JavaClassType> arguments) {
        try {
            Class<?> methodOwnerClass = owner.getTypeClass();
            Class<?>[] params = arguments.stream()
                    .map(JavaClassType::getTypeClass).toArray(Class<?>[]::new);
            Method method = MethodUtils.getMatchingAccessibleMethod(methodOwnerClass, methodName, params);
            return Optional.of(ReflectionObjectToSignatureMapper.fromMethod(method));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<com.kubadziworski.domain.scope.Field> getFieldSignature(JavaClassType owner, String fieldName) {
        try {
            Field field = FieldUtils.getField(owner.getTypeClass(), fieldName);
            return Optional.of(ReflectionObjectToSignatureMapper.fromField(field, owner));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<FunctionSignature> getConstructorSignature(JavaClassType className, List<JavaClassType> arguments) {
        try {
            Class<?> methodOwnerClass = className.getTypeClass();
            Class<?>[] params = arguments.stream()
                    .map(JavaClassType::getTypeClass).toArray(Class<?>[]::new);
            Constructor<?> constructor = ConstructorUtils.getMatchingAccessibleConstructor(methodOwnerClass, params);
            return Optional.of(ReflectionObjectToSignatureMapper.fromConstructor(constructor, className));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
