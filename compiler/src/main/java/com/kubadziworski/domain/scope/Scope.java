package com.kubadziworski.domain.scope;

import com.google.common.collect.Lists;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.exception.FieldNotFoundException;
import com.kubadziworski.exception.LocalVariableNotFoundException;
import com.kubadziworski.exception.MethodSignatureNotFoundException;
import com.kubadziworski.exception.MethodWithNameAlreadyDefinedException;
import com.kubadziworski.resolver.ImportResolver;
import com.kubadziworski.util.TypeResolver;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


public class Scope {
    private final List<FunctionSignature> functionSignatures;
    private final List<FunctionSignature> constructorSignatures;
    private final LinkedMap<String, LocalVariable> localVariables;
    private final Map<String, Field> fields;
    private final ImportResolver importResolver;
    private final MetaData metaData;
    private final List<Function> methods;

    private final FunctionSignature currentFunctionSignature;

    public Scope(MetaData metaData, ImportResolver importResolver) {
        this.metaData = metaData;
        functionSignatures = new ArrayList<>();
        constructorSignatures = new ArrayList<>();
        localVariables = new LinkedMap<>();
        fields = new LinkedMap<>();
        this.importResolver = importResolver;
        this.currentFunctionSignature = null;
        this.methods = new ArrayList<>();
    }

    public Scope(Scope scope) {
        metaData = scope.metaData;
        functionSignatures = Lists.newArrayList(scope.getFunctionSignatures());
        constructorSignatures = Lists.newArrayList(scope.getConstructorSignatures());
        fields = new LinkedMap<>(scope.getFields());
        localVariables = new LinkedMap<>(scope.getLocalVariables());
        this.importResolver = scope.getImportResolver();
        this.currentFunctionSignature = scope.currentFunctionSignature;
        this.methods = scope.methods;
    }

    public Scope(Scope scope, FunctionSignature functionSignature) {
        metaData = scope.metaData;
        functionSignatures = Lists.newArrayList(scope.getFunctionSignatures());
        constructorSignatures = Lists.newArrayList(scope.getConstructorSignatures());
        fields = new LinkedMap<>(scope.getFields());
        localVariables = new LinkedMap<>(scope.getLocalVariables());
        this.importResolver = scope.getImportResolver();
        this.currentFunctionSignature = functionSignature;
        this.methods = scope.methods;
    }

    public void addSignature(FunctionSignature signature) {
        List<ArgumentHolder> holders = signature.getParameters().stream().map(parameter -> new ArgumentHolder(parameter.getType(), parameter.getName()))
                .collect(Collectors.toList());
        if (isSignatureExists(signature.getName(), holders, functionSignatures)) {
            throw new MethodWithNameAlreadyDefinedException(signature);
        }
        functionSignatures.add(signature);
    }

    public void addConstructor(FunctionSignature signature) {
        List<ArgumentHolder> holders = signature.getParameters().stream().map(parameter -> new ArgumentHolder(parameter.getType(), parameter.getName()))
                .collect(Collectors.toList());
        if (isSignatureExists(signature.getName(), holders, constructorSignatures)) {
            throw new MethodWithNameAlreadyDefinedException(signature);
        }
        constructorSignatures.add(signature);
    }

    public Map<String, LocalVariable> getLocalVariables() {
        return localVariables;
    }


    private boolean isSignatureExists(String identifier, List<ArgumentHolder> arguments, List<FunctionSignature> signatures) {
        if (identifier.equals("super")) return true;

        Map<Integer, List<FunctionSignature>> functions = signatures.stream()
                .collect(Collectors.groupingBy(signature -> signature.matches(identifier, arguments)));

        return TypeResolver.resolveArity(this.getClassType(), functions).isPresent();
    }


    public FunctionSignature getMethodCallSignature(String identifier, List<ArgumentHolder> arguments) {
        if (identifier.equals("super")) {
            return new FunctionSignature("super", Collections.emptyList(), VoidType.INSTANCE, Modifiers.empty().with(Modifier.PUBLIC),
                    getSuperClassType(), SignatureType.CONSTRUCTOR_CALL);
        }

        Map<Integer, List<FunctionSignature>> functions = functionSignatures.stream()
                .collect(Collectors.groupingBy(signature -> signature.matches(identifier, arguments)));

        return TypeResolver.resolveArity(getClassType(), functions).map(Optional::of).orElseGet(() -> importResolver.getMethod(identifier, arguments))
                .orElseThrow(() -> new MethodSignatureNotFoundException(this, identifier, arguments));
    }

    public Type getSuperClassType() {
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
        //We need for each type size increase the index position of the variable to take into account double sized
        //types like double and long, this should be later cleaned up as it is binding the Scope to the jvm generator
        int indexPosition = 0;
        for (Map.Entry<String, LocalVariable> entry : localVariables.entrySet()) {
            if (entry.getKey().equals(varName)) {
                return indexPosition;
            } else {
                indexPosition += entry.getValue().getType().getAsmType().getSize();
            }
        }
        throw new LocalVariableNotFoundException(this, varName);
    }

    public boolean isLocalVariableExists(String varName) {
        return localVariables.containsKey(varName);
    }

    public void addField(Field field) {
        fields.put(field.getName(), field);

        if (field.getSetterFunction() != null) {
            functionSignatures.add(field.getSetterFunction().getFunctionSignature());
        }
        if (field.getGetterFunction() != null) {
            functionSignatures.add(field.getGetterFunction().getFunctionSignature());
        }
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

    public final void addMethods(List<Function> functions) {
        methods.addAll(functions);
    }

    public List<Function> getMethods() {
        return methods;
    }

    public List<FunctionSignature> getConstructorSignatures() {
        return constructorSignatures;
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


    public MetaData getMetaData() {
        return metaData;
    }

    public Type getClassType() {
        String className = getFullClassName();
        return new EnkelType(className, this);
    }

    private ImportResolver getImportResolver() {
        return importResolver;
    }

    public Type resolveClassName(String className) {
        Optional<Type> clazz = importResolver.getClass(className);
        if (clazz.isPresent()) {
            return clazz.get();
        }
        return ClassTypeFactory.createClassType(className);
    }

    public Scope cloneWithoutVariables() {
        Scope scope = new Scope(this);
        scope.localVariables.clear();
        return scope;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "name=" + getClassType().getName() +
                '}';
    }
}
