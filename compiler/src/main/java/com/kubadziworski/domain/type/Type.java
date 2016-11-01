package com.kubadziworski.domain.type;

import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.exception.FieldNotFoundException;
import com.kubadziworski.exception.MethodSignatureNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Created by kuba on 28.03.16.
 */
public interface Type {
    String getName();

    Class<?> getTypeClass();

    String getDescriptor();

    String getInternalName();

    Optional<Type> getSuperType();

    List<Field> getFields();

    List<FunctionSignature> getFunctionSignatures();

    boolean inheritsFrom(Type type);

    int getDupCode();

    int getDupX1Code();

    int getLoadVariableOpcode();

    int getStoreVariableOpcode();

    int getReturnOpcode();

    int getAddOpcode();

    int getSubstractOpcode();

    int getMultiplyOpcode();

    int getDividOpcode();

    int getNegation();

    int getStackSize();


    default FunctionSignature getMethodCallSignature(String identifier, List<Argument> arguments) {
        List<FunctionSignature> signatures = getFunctionSignatures();
        return signatures.stream()
                .filter(signature -> signature.matches(identifier, arguments))
                .findFirst().orElseThrow(() -> new MethodSignatureNotFoundException(identifier, arguments));
    }

    default Field getField(String fieldName) {
        List<Field> fields = getFields();
        return fields.stream().filter(field -> field.getName().equals(fieldName))
                .findAny().orElseThrow(() -> new FieldNotFoundException(this, fieldName));

    }
}
