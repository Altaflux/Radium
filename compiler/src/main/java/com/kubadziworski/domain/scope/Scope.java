package com.kubadziworski.domain.scope;

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
    private final Map<String, Field> fields;
    private final ImportResolver importResolver;
    private final MetaData metaData;
    private final List<Function> methods;

    public Scope(MetaData metaData, ImportResolver importResolver) {
        this.metaData = metaData;
        this.functionSignatures = new ArrayList<>();
        this.constructorSignatures = new ArrayList<>();
        this.fields = new LinkedMap<>();
        this.importResolver = importResolver;
        this.methods = new ArrayList<>();
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


    private boolean isSignatureExists(String identifier, List<ArgumentHolder> arguments, List<FunctionSignature> signatures) {
        if (identifier.equals("super")) return true;

        Map<Integer, List<FunctionSignature>> functions = signatures.stream()
                .collect(Collectors.groupingBy(signature -> signature.matches(identifier, arguments)));

        return TypeResolver.resolveArity(this.getClassType(), functions).isPresent();
    }


    FunctionSignature getMethodCallSignature(String identifier, List<ArgumentHolder> arguments) {
        if (identifier.equals("super")) {
            return new FunctionSignature("super", Collections.emptyList(), VoidType.INSTANCE, Modifiers.empty().with(Modifier.PUBLIC),
                    getSuperClassType(), SignatureType.CONSTRUCTOR_CALL);
        }

        Map<Integer, List<FunctionSignature>> functions = functionSignatures.stream()
                .collect(Collectors.groupingBy(signature -> signature.matches(identifier, arguments)));

        return TypeResolver.resolveArity(getClassType(), functions).map(Optional::of)
                .orElseGet(() -> importResolver.getMethod(identifier, arguments))
                .map(Optional::of).orElseGet(() -> getSuperMethods(identifier, arguments))
                .orElseThrow(() -> new MethodSignatureNotFoundException(this, identifier, arguments));

    }

    private Optional<FunctionSignature> getSuperMethods(String identifier, List<ArgumentHolder> arguments) {
        try {
            return getClassType().getSuperType().map(type -> type.getMethodCallSignature(identifier, arguments));
        } catch (MethodSignatureNotFoundException e) {
            return Optional.empty();
        }
    }

    public Type getSuperClassType() {
        return metaData.getSuperClass();
    }


    public void addField(Field field) {
        fields.put(field.getName(), field);
        field.getSetterFunction().ifPresent(function -> functionSignatures.add(function.getFunctionSignature()));
        field.getGetterFunction().ifPresent(function -> functionSignatures.add(function.getFunctionSignature()));
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

    boolean isFieldExists(String varName) {
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
