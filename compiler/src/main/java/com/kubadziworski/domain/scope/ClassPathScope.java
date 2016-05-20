package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import com.kubadziworski.util.ReflectionObjectToSignatureMapper;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

/**
 * Created by kuba on 11.05.16.
 */
public class ClassPathScope {

    public Optional<FunctionSignature> getMethodSignature(Type owner, String methodName, List<Type> arguments) {
        try {
            Class<?> methodOwnerClass = owner.getTypeClass();
            Class<?>[] params = arguments.stream()
                    .map(Type::getTypeClass).toArray(Class<?>[]::new);
            Method method = MethodUtils.getMatchingAccessibleMethod(methodOwnerClass, methodName, params);
            return Optional.of(ReflectionObjectToSignatureMapper.fromMethod(method));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<FunctionSignature> getConstructorSignature(String className, List<Type> arguments) {
        try {
            Class<?> methodOwnerClass = Class.forName(className);
            Class<?>[] params = arguments.stream()
                    .map(Type::getTypeClass).toArray(Class<?>[]::new);
            Constructor<?> constructor = ConstructorUtils.getMatchingAccessibleConstructor(methodOwnerClass,params);
            return Optional.of(ReflectionObjectToSignatureMapper.fromConstructor(constructor));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
