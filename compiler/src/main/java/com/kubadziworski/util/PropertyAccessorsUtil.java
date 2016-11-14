package com.kubadziworski.util;

import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.EnkelType;

import com.kubadziworski.domain.type.UnitType;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class PropertyAccessorsUtil {


    private static final Set<Class<?>> ANY_TYPES = Collections.emptySet();

    private static final Set<Class<?>> BOOLEAN_TYPES;

    static {
        Set<Class<?>> booleanTypes = new HashSet<>();
        booleanTypes.add(Boolean.class);
        booleanTypes.add(Boolean.TYPE);
        BOOLEAN_TYPES = Collections.unmodifiableSet(booleanTypes);
    }


    private static Method findGetterForProperty(String propertyName, Class<?> clazz, boolean mustBeStatic) {
        Method method = findMethodForProperty(getPropertyMethodSuffixes(propertyName),
                "get", clazz, mustBeStatic, 0, ANY_TYPES);
        if (method == null) {
            method = findMethodForProperty(getPropertyMethodSuffixes(propertyName),
                    "is", clazz, mustBeStatic, 0, BOOLEAN_TYPES);
        }
        return method;
    }

    private static Method findSetterForProperty(String propertyName, Class<?> clazz, boolean mustBeStatic) {
        return findMethodForProperty(getPropertyMethodSuffixes(propertyName),
                "set", clazz, mustBeStatic, 1, ANY_TYPES);
    }


    public static FunctionSignature createSetterForField(Field field, String fieldName) {
        return new FunctionSignature("set" + getPropertyMethodSuffix(field.getName()),
                Collections.singletonList(new Parameter(fieldName, field.getType(), null)),
                UnitType.INSTANCE, Modifier.PUBLIC, field.getOwner());
    }

    public static FunctionSignature createSetterForField(Field field) {
        return createSetterForField(field, field.getName());
    }

    public static FunctionSignature createGetterForField(Field field) {
        if (field.getType().equals(BuiltInType.BOOLEAN)) {
            return new FunctionSignature("is" + getPropertyMethodSuffix(field.getName()), Collections.emptyList(),
                    field.getType(), Modifier.PUBLIC, field.getOwner());
        }
        return new FunctionSignature("get" + getPropertyMethodSuffix(field.getName()), Collections.emptyList(),
                field.getType(), Modifier.PUBLIC, field.getOwner());
    }


    public static Optional<FunctionSignature> getSetterFunctionSignatureForField(Field field) {
        try {
            Class clazz = field.getOwner().getTypeClass();
            Method method = findSetterForProperty(field.getName(), clazz, Modifier.isStatic(field.getModifiers()));
            if (method != null) {
                return Optional.of(ReflectionObjectToSignatureMapper.fromMethod(method));
            }
            return Optional.empty();
        } catch (Exception e) {
            return ((EnkelType) field.getOwner()).getScope()
                    .map(scope -> getFunctionSignatureForMethod("set", field, scope,
                            Collections.singletonList(new Argument(new EmptyExpression(field.getType()), null))));
        }
    }

    public static Optional<FunctionSignature> getGetterFunctionSignatureForField(Field field) {
        try {
            Class clazz = field.getOwner().getTypeClass();
            Method method = findGetterForProperty(field.getName(), clazz, Modifier.isStatic(field.getModifiers()));
            if (method != null) {
                return Optional.of(ReflectionObjectToSignatureMapper.fromMethod(method));
            }
            return Optional.empty();
        } catch (Exception e) {
            return ((EnkelType) field.getOwner()).getScope().map(scope -> {
                if (field.getType().equals(BuiltInType.BOOLEAN)) {
                    return getFunctionSignatureForMethod("is", field, scope, Collections.emptyList());
                }
                return getFunctionSignatureForMethod("get", field, scope, Collections.emptyList());
            });
        }
    }

    private static FunctionSignature getFunctionSignatureForMethod(String prefix, Field field, Scope scope, List<Argument> arguments) {
        for (String suffix : getPropertyMethodSuffixes(field.getName())) {
            try {
                return scope.getMethodCallSignature(prefix + suffix, arguments);
            } catch (Exception ie) {
                //
            }
        }
        return null;
    }

    private static Method findMethodForProperty(String[] methodSuffixes, String prefix, Class<?> clazz,
                                                boolean mustBeStatic, int numberOfParams, Set<Class<?>> requiredReturnTypes) {

        Method[] methods = getSortedClassMethods(clazz);
        for (String methodSuffix : methodSuffixes) {
            for (Method method : methods) {
                if (method.getName().equals(prefix + methodSuffix) &&
                        method.getParameterCount() == numberOfParams &&
                        (!mustBeStatic || Modifier.isStatic(method.getModifiers())) &&
                        (requiredReturnTypes.isEmpty() || requiredReturnTypes.contains(method.getReturnType()))) {
                    return method;
                }
            }
        }
        return null;
    }

    private static Method[] getSortedClassMethods(Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        Arrays.sort(methods, (o1, o2) -> (o1.isBridge() == o2.isBridge()) ? 0 : (o1.isBridge() ? 1 : -1));
        return methods;
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
