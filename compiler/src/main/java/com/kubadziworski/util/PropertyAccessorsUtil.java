package com.kubadziworski.util;

import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.*;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Optional;


public class PropertyAccessorsUtil {


    public static FunctionSignature createSetterForField(Field field, String fieldName) {
        boolean staticField = field.getModifiers().contains(Modifier.STATIC);
        Modifiers modifiers = Modifiers.empty().with(Modifier.FINAL).with(Modifier.PUBLIC);
        if (staticField) {
            modifiers = modifiers.with(Modifier.STATIC);
        }
        SignatureType signatureType = SignatureType.FUNCTION_CALL;

        return new FunctionSignature("set" + getPropertyMethodSuffix(field.getName()),
                Collections.singletonList(new Parameter(fieldName, field.getType(), null)),
                VoidType.INSTANCE, modifiers, field.getOwner(), signatureType);
    }

    public static FunctionSignature createSetterForField(Field field) {
        return createSetterForField(field, field.getName());
    }

    public static FunctionSignature createGetterForField(Field field) {
        boolean staticField = field.getModifiers().contains(Modifier.STATIC);
        Modifiers modifiers = Modifiers.empty().with(Modifier.FINAL).with(Modifier.PUBLIC);
        if (staticField) {
            modifiers = modifiers.with(Modifier.STATIC);
        }
        SignatureType signatureType = SignatureType.FUNCTION_CALL;

        if (field.getType().equals(PrimitiveTypes.BOOLEAN_TYPE)) {
            return new FunctionSignature("is" + getPropertyMethodSuffix(field.getName()), Collections.emptyList(),
                    field.getType(), modifiers, field.getOwner(), signatureType);
        }
        return new FunctionSignature("get" + getPropertyMethodSuffix(field.getName()), Collections.emptyList(),
                field.getType(), modifiers, field.getOwner(), signatureType);
    }

    public static Optional<FunctionSignature> getSetterFunctionSignatureForField(Field field) {
        return findSetterForProperty(field, field.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC));
    }

    public static Optional<FunctionSignature> getGetterFunctionSignatureForField(Field field) {
        return findGetterForProperty(field, field.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC));
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
                        (!mustBeStatic || signature.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC)) &&
                        (requiredReturnTypes == null || signature.getReturnType().inheritsFrom(requiredReturnTypes) > -1)) {

                    return Optional.of(signature);
                }
            }
        }
        return Optional.empty();
    }


    public static boolean isFunctionAccessible(CallableDescriptor signature, Type caller) {
        if (signature.getModifiers().contains(Modifier.PUBLIC)) {
            return true;
        }
        if (signature.getModifiers().contains(Modifier.PRIVATE) && !signature.getOwner().getName()
                .equals(caller.getName())) {
            return false;
        }
        if (signature.getModifiers().contains(Modifier.PROTECTED)) {
            if (caller.inheritsFrom(signature.getOwner()) < 0) {
                return false;
            }
        }
        //Has to be package protected
        return caller.getPackage().equals(signature.getOwner().getPackage());
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


    public static Function generateGetter(Field field, Scope scope) {
        FunctionSignature getter = PropertyAccessorsUtil.createGetterForField(field);
        Scope newScope = new Scope(scope);
        newScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        FieldReference fieldReference = new FieldReference(field, new LocalVariableReference(newScope.getLocalVariable("this")));
        ReturnStatement returnStatement = new ReturnStatement(fieldReference);
        Block block = new Block(newScope, Collections.singletonList(returnStatement));
        return new Function(getter, block);
    }

    public static Function generateSetter(Field field, Scope scope) {
        if (field.getModifiers().contains(Modifier.FINAL)) {
            return null;
        }
        FunctionSignature getter = PropertyAccessorsUtil.createSetterForField(field);
        Scope newScope = new Scope(scope);
        newScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        getter.getParameters()
                .forEach(param -> newScope.addLocalVariable(new LocalVariable(param.getName(), param.getType(), false, param.isVisible())));
        LocalVariableReference localVariableReference = new LocalVariableReference(new LocalVariable(field.getName(), field.getType()));
        LocalVariableReference thisReference = new LocalVariableReference(newScope.getLocalVariable("this"));

        FieldAssignment assignment = new FieldAssignment(thisReference, field, localVariableReference);
        Block block = new Block(newScope, Collections.singletonList(assignment));
        return new Function(getter, block);
    }
}
