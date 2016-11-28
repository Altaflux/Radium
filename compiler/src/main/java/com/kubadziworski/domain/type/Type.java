package com.kubadziworski.domain.type;

import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.exception.FieldNotFoundException;
import com.kubadziworski.exception.MethodSignatureNotFoundException;
import com.kubadziworski.util.TypeResolver;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by kuba on 28.03.16.
 */
public interface Type {

    enum Nullability {
        NULLABLE, NOT_NULL, UNKNOWN
    }

    String getName();

    Class<?> getTypeClass();

    String getDescriptor();

    String getInternalName();

    Optional<Type> getSuperType();

    List<Field> getFields();

    List<FunctionSignature> getFunctionSignatures();

    int inheritsFrom(Type type);

    Optional<Type> nearestDenominator(Type type);

    int getDupCode();

    int getDupX1Code();

    int getLoadVariableOpcode();

    int getStoreVariableOpcode();

    int getReturnOpcode();

    boolean isPrimitive();

    Nullability isNullable();

    default FunctionSignature getMethodCallSignature(String identifier, List<ArgumentHolder> arguments) {
        List<FunctionSignature> signatures = getFunctionSignatures();
        Map<Integer, List<FunctionSignature>> functions = signatures.stream()
                .collect(Collectors.groupingBy(signature -> signature.matches(identifier, arguments)));

        return TypeResolver.resolveArity(this, functions).orElseThrow(() -> new MethodSignatureNotFoundException(identifier, arguments, this));
    }

    default Field getField(String fieldName) {
        List<Field> fields = getFields();
        return fields.stream().filter(field -> field.getName().equals(fieldName))
                .findAny().orElseThrow(() -> new FieldNotFoundException(this, fieldName));
    }
}
