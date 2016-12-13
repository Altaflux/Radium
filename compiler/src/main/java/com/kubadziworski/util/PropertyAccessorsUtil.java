package com.kubadziworski.util;

import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import org.springframework.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Optional;


public class PropertyAccessorsUtil {


    public static FunctionSignature createSetterForField(Field field, String fieldName) {
        return new FunctionSignature("set" + getPropertyMethodSuffix(field.getName()),
                Collections.singletonList(new Parameter(fieldName, field.getType(), null)),
                VoidType.INSTANCE, Modifier.PUBLIC + Modifier.FINAL, field.getOwner());
    }

    public static FunctionSignature createSetterForField(Field field) {
        return createSetterForField(field, field.getName());
    }

    public static FunctionSignature createGetterForField(Field field) {
        if (field.getType().equals(PrimitiveTypes.BOOLEAN_TYPE)) {
            return new FunctionSignature("is" + getPropertyMethodSuffix(field.getName()), Collections.emptyList(),
                    field.getType(), Modifier.PUBLIC + Modifier.FINAL, field.getOwner());
        }
        return new FunctionSignature("get" + getPropertyMethodSuffix(field.getName()), Collections.emptyList(),
                field.getType(), Modifier.PUBLIC + Modifier.FINAL, field.getOwner());
    }

    public static Optional<FunctionSignature> getSetterFunctionSignatureForField(Field field) {
        return findSetterForProperty(field, Modifier.isStatic(field.getModifiers()));
    }

    public static Optional<FunctionSignature> getGetterFunctionSignatureForField(Field field) {
        return findGetterForProperty(field, Modifier.isStatic(field.getModifiers()));
    }


    private static Optional<FunctionSignature> findGetterForProperty(Field field, boolean mustBeStatic) {
        String[] possibleNames = getPropertyMethodSuffixes(field.getName());
        Optional<FunctionSignature> method = findMethodForProperty(possibleNames, "get", field.getOwner(), mustBeStatic, 0, field.getType());
        if (method.isPresent()) {
            return method;
        }
        return findMethodForProperty(possibleNames, "is", field.getOwner(), mustBeStatic, 0, field.getType());
    }

    private static Optional<FunctionSignature> findSetterForProperty(Field field, boolean mustBeStatic) {
        return findMethodForProperty(getPropertyMethodSuffixes(field.getName()), "set", field.getOwner(), mustBeStatic, 1, null);
    }


    private static Optional<FunctionSignature> findMethodForProperty(String[] methodSuffixes, String prefix, Type type,
                                                                     boolean mustBeStatic, int numberOfParams, Type requiredReturnTypes) {
        for (String methodSuffix : methodSuffixes) {
            for (FunctionSignature signature : type.getFunctionSignatures()) {
                if (signature.getName().equals(prefix + methodSuffix) &&
                        signature.getParameters().size() == numberOfParams &&
                        (!mustBeStatic || Modifier.isStatic(signature.getModifiers())) &&
                        (requiredReturnTypes == null || signature.getReturnType().inheritsFrom(requiredReturnTypes) > -1)) {

                    return Optional.of(signature);
                }
            }
        }
        return Optional.empty();
    }


    /**
     * Return the method suffixes for a given property name. The default implementation
     * uses JavaBean conventions with additional support for properties of the form 'xY'
     * where the method 'getXY()' is used in preference to the JavaBean convention of
     * 'getxY()'.
     */
    private static String[] getPropertyMethodSuffixes(String propertyName) {
        String suffix = getPropertyMethodSuffix(propertyName);
        if (suffix.length() > 0 && Character.isUpperCase(suffix.charAt(0))) {
            return new String[]{suffix};
        }
        return new String[]{suffix, StringUtils.capitalize(suffix)};
    }

    /**
     * Return the method suffix for a given property name. The default implementation
     * uses JavaBean conventions.
     */
    private static String getPropertyMethodSuffix(String propertyName) {
        if (propertyName.length() > 1 && Character.isUpperCase(propertyName.charAt(1))) {
            return propertyName;
        }
        return StringUtils.capitalize(propertyName);
    }
}
