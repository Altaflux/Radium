package com.kubadziworski.domain.scope;

import com.google.common.collect.Lists;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.resolver.ImportResolver;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.exception.FieldNotFoundException;
import com.kubadziworski.exception.LocalVariableNotFoundException;
import com.kubadziworski.exception.MethodSignatureNotFoundException;
import com.kubadziworski.exception.MethodWithNameAlreadyDefinedException;
import com.kubadziworski.util.TypeResolver;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


public class Scope {
    private final List<FunctionSignature> functionSignatures;

    private final LinkedMap<String, LocalVariable> localVariables;
    private final Map<String, Field> fields;
    private final ImportResolver importResolver;
    private final MetaData metaData;
    private final EnkelScope enkelScope;

    private final FunctionSignature currentFunctionSignature;

    public Scope(MetaData metaData, ImportResolver importResolver) {
        this.metaData = metaData;
        functionSignatures = new ArrayList<>();
        localVariables = new LinkedMap<>();
        fields = new LinkedMap<>();
        this.importResolver = importResolver;
        this.enkelScope = new EnkelScope(importResolver.getGlobalScope());
        this.currentFunctionSignature = null;
    }

    public Scope(Scope scope) {
        metaData = scope.metaData;
        functionSignatures = Lists.newArrayList(scope.getFunctionSignatures());
        fields = new LinkedMap<>(scope.getFields());
        localVariables = new LinkedMap<>(scope.getLocalVariables());
        this.importResolver = scope.getImportResolver();
        this.enkelScope = scope.enkelScope;
        this.currentFunctionSignature = scope.currentFunctionSignature;
    }

    public Scope(Scope scope, FunctionSignature functionSignature) {
        metaData = scope.metaData;
        functionSignatures = Lists.newArrayList(scope.getFunctionSignatures());
        fields = new LinkedMap<>(scope.getFields());
        localVariables = new LinkedMap<>(scope.getLocalVariables());
        this.importResolver = scope.getImportResolver();
        this.enkelScope = scope.enkelScope;
        this.currentFunctionSignature = functionSignature;
    }

    public void addSignature(FunctionSignature signature) {
        if (isParameterLessSignatureExists(signature.getName())) {
            throw new MethodWithNameAlreadyDefinedException(signature);
        }
        functionSignatures.add(signature);
    }

    public Map<String, LocalVariable> getLocalVariables() {
        return localVariables;
    }

    public boolean isParameterLessSignatureExists(String identifier) {
        return isSignatureExists(identifier, Collections.emptyList());
    }

    public boolean isSignatureExists(String identifier, List<ArgumentHolder> arguments) {
        if (identifier.equals("super")) return true;

        Map<Integer, List<FunctionSignature>> functions = functionSignatures.stream()
                .collect(Collectors.groupingBy(signature -> signature.matches(identifier, arguments)));

        return TypeResolver.resolveArity(this.getClassType(), functions).isPresent();
    }

    public FunctionSignature getMethodCallSignatureWithoutParameters(String identifier) {
        return getMethodCallSignature(identifier, Collections.emptyList());
    }

    public FunctionSignature getConstructorCallSignature(String className, List<ArgumentHolder> arguments) {
        //TODO Think about how to resolve is if another class name is the same as the local class
        boolean isDifferentThanCurrentClass = !className.equals(getFullClassName());
        if (isDifferentThanCurrentClass) {

            List<Type> argumentsTypes = arguments.stream().map(argumentStub -> argumentStub.getExpression().getType()).collect(toList());
            Type resolvedClass = resolveClassName(className);
            return enkelScope.getConstructorSignature(resolvedClass, arguments).map(Optional::of)
                    .orElse(new ClassPathScope().getConstructorSignature(resolvedClass, argumentsTypes))
                    .orElseThrow(() -> new MethodSignatureNotFoundException(this, resolvedClass.getName(), arguments));
        }
        return getConstructorCallSignatureForCurrentClass(arguments);
    }

    private FunctionSignature getConstructorCallSignatureForCurrentClass(List<ArgumentHolder> arguments) {
        return getMethodCallSignature(null, getFullClassName(), arguments);
    }

    private FunctionSignature getMethodCallSignature(Type owner, String methodName, List<ArgumentHolder> arguments) {
        boolean isDifferentThanCurrentClass = owner != null && !owner.equals(getClassType());
        if (isDifferentThanCurrentClass) {

            List<Type> argumentsTypes = arguments.stream().map(argumentStub -> argumentStub.getExpression().getType()).collect(toList());
            return enkelScope.getMethodSignature(owner, methodName, arguments).map(Optional::of)
                    .orElse(new ClassPathScope().getMethodSignature(owner, methodName, argumentsTypes))
                    .orElseThrow(() -> new MethodSignatureNotFoundException(this, methodName, arguments));
        }
        return getMethodCallSignature(methodName, arguments);
    }

    public FunctionSignature getMethodCallSignature(String identifier, List<ArgumentHolder> arguments) {
        if (identifier.equals("super")) {
            return new FunctionSignature("super", Collections.emptyList(), VoidType.INSTANCE, Modifier.PUBLIC,
                    ClassTypeFactory.createClassType(getSuperClassName()));
        }

        Map<Integer, List<FunctionSignature>> functions = functionSignatures.stream()
                .collect(Collectors.groupingBy(signature -> signature.matches(identifier, arguments)));

        return TypeResolver.resolveArity(getClassType(), functions).map(Optional::of).orElseGet(() -> importResolver.getMethod(identifier, arguments))
                .orElseThrow(() -> new MethodSignatureNotFoundException(this, identifier, arguments));
    }

    public String getSuperClassName() {
        return metaData.getSuperClass();
    }

    public void addLocalVariable(LocalVariable variable) {
        localVariables.put(variable.getName(), variable);
    }


    public FunctionSignature getCurrentFunctionSignature() {
        return currentFunctionSignature;
    }

    public LocalVariable getLocalVariable(String varName) {
        return Optional.ofNullable(localVariables.get(varName))
                .orElseThrow(() -> new LocalVariableNotFoundException(this, varName));
    }

    public int getLocalVariableIndex(String varName) {
        return localVariables.indexOf(varName);
    }

    public boolean isLocalVariableExists(String varName) {
        return localVariables.containsKey(varName);
    }

    public void addField(Field field) {
        fields.put(field.getName(), field);
    }

    public void addField(String name, Field field) {
        fields.put(name, field);
    }

    public Map<String, Field> getFields() {
        return fields;
    }

    public List<FunctionSignature> getFunctionSignatures() {
        return functionSignatures;
    }

    public Field getField(Type owner, String fieldName) {
        boolean isDifferentThanCurrentClass = owner != null && !owner.equals(getClassType());
        if (!isDifferentThanCurrentClass) {
            return getField(fieldName);
        }
        return enkelScope.getFieldSignature(owner, fieldName).map(Optional::of)
                .orElse(new ClassPathScope().getFieldSignature(owner, fieldName))
                .orElseThrow(() -> new FieldNotFoundException(this, fieldName));
    }

    public Field getField(String fieldName) {
        return Optional.ofNullable(fields.get(fieldName)).map(Optional::of).orElse(importResolver.getField(fieldName))
                .orElseThrow(() -> new FieldNotFoundException(this, fieldName));
    }

    public boolean isFieldExists(String varName) {
        return fields.containsKey(varName);
    }

    public String getFullClassName() {
        if (StringUtils.isNotEmpty(metaData.getPackageName())) {
            return metaData.getPackageName() + "." + metaData.getClassName();
        }
        return metaData.getClassName();
    }

    public String getClassName() {
        return metaData.getClassName();
    }

    public String getSuperClassInternalName() {
        return ClassTypeFactory.createClassType(getSuperClassName()).getAsmType().getInternalName();
    }

    public Type getClassType() {
        String className = getFullClassName();
        return new EnkelType(className, this);
    }

    public String getClassInternalName() {
        return getClassType().getAsmType().getInternalName();
    }

    public ImportResolver getImportResolver() {
        return importResolver;
    }

    public Type resolveClassName(String className) {
        Optional<Type> clazz = importResolver.getClass(className);
        if (clazz.isPresent()) {
            return clazz.get();
        }
        return ClassTypeFactory.createClassType(className);
    }



    @Override
    public String toString() {
        return "Scope{" +
                "name=" + getClassType().getName() +
                '}';
    }
}
