package com.kubadziworski.domain.scope;

import com.google.common.collect.Lists;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.resolver.ImportResolver;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.FieldNotFoundException;
import com.kubadziworski.exception.LocalVariableNotFoundException;
import com.kubadziworski.exception.MethodSignatureNotFoundException;
import com.kubadziworski.exception.MethodWithNameAlreadyDefinedException;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.stream.Collectors.toList;


public class Scope {
    private final List<FunctionSignature> functionSignatures;

    private final LinkedMap<String, LocalVariable> localVariables;
    private final Map<String, Field> fields;
    private final ImportResolver imports;
    private final MetaData metaData;
    private final EnkelScope enkelScope;

    public Scope(MetaData metaData, ImportResolver imports) {
        this.metaData = metaData;
        functionSignatures = new ArrayList<>();
        localVariables = new LinkedMap<>();
        fields = new LinkedMap<>();
        this.imports = imports;
        this.enkelScope = new EnkelScope(imports);
    }

    public Scope(Scope scope) {
        metaData = scope.metaData;
        functionSignatures = Lists.newArrayList(scope.functionSignatures);
        fields = new LinkedMap<>(scope.fields);
        localVariables = new LinkedMap<>(scope.localVariables);
        this.imports = scope.getImports();
        this.enkelScope = scope.enkelScope;
    }

    public void resolveImports() {
        imports.parseImports();
    }

    public void partialImportResolve() {
        imports.doPreClassParse();
    }

    public void addSignature(FunctionSignature signature) {
        if (isParameterLessSignatureExists(signature.getName())) {
            throw new MethodWithNameAlreadyDefinedException(signature);
        }
        functionSignatures.add(signature);
    }

    public boolean isParameterLessSignatureExists(String identifier) {
        return isSignatureExists(identifier, Collections.emptyList());
    }

    public boolean isSignatureExists(String identifier, List<Argument> arguments) {
        if (identifier.equals("super")) return true;

        return functionSignatures.stream()
                .anyMatch(signature -> signature.matches(identifier, arguments));
    }

    public FunctionSignature getMethodCallSignatureWithoutParameters(String identifier) {
        return getMethodCallSignature(identifier, Collections.emptyList());
    }

    public FunctionSignature getConstructorCallSignature(String className, List<Argument> arguments) {
        //TODO Think about how to resolve is if another class name is the same as the local class
        boolean isDifferentThanCurrentClass = !className.equals(getFullClassName());
        if (isDifferentThanCurrentClass) {

            List<Type> argumentsTypes = arguments.stream().map(Argument::getType).collect(toList());
            ClassType resolvedClass = resolveClassName(className);
           return enkelScope.getConstructorSignature(resolvedClass, arguments).map(Optional::of)
                    .orElse(new ClassPathScope().getConstructorSignature(resolvedClass, argumentsTypes))
                    .orElseThrow(() -> new MethodSignatureNotFoundException(this, resolvedClass.getName(), arguments));
        }
        return getConstructorCallSignatureForCurrentClass(arguments);
    }

    private FunctionSignature getConstructorCallSignatureForCurrentClass(List<Argument> arguments) {
        return getMethodCallSignature(null, getFullClassName(), arguments);
    }

    public FunctionSignature getMethodCallSignature(Type owner, String methodName, List<Argument> arguments) {
        boolean isDifferentThanCurrentClass = owner != null && !owner.equals(getClassType());
        if (isDifferentThanCurrentClass) {

            List<Type> argumentsTypes = arguments.stream().map(Argument::getType).collect(toList());
           return enkelScope.getMethodSignature(owner, methodName, arguments).map(Optional::of)
                    .orElse(new ClassPathScope().getMethodSignature(owner, methodName, argumentsTypes))
                    .orElseThrow(() -> new MethodSignatureNotFoundException(this, methodName, arguments));
        }
        return getMethodCallSignature(methodName, arguments);
    }

    public FunctionSignature getMethodCallSignature(String identifier, List<Argument> arguments) {
        if (identifier.equals("super")) {
            //TODO Set modifiers correctly
            return new FunctionSignature("super", Collections.emptyList(), BultInType.VOID, Modifier.PUBLIC, new ClassType(getSuperClassName()));
        }
        Optional<FunctionSignature> function = functionSignatures.stream()
                .filter(signature -> signature.matches(identifier, arguments))
                .findFirst();

        return function.map(Optional::of).orElse(imports.getMethod(identifier, arguments))
                .orElseThrow(() -> new MethodSignatureNotFoundException(this, identifier, arguments));

    }

    private String getSuperClassName() {
        //TODO SET CORRECTLY
        return "java.lang.Object";
    }

    public void addLocalVariable(LocalVariable variable) {
        localVariables.put(variable.getName(), variable);
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
        return Optional.ofNullable(fields.get(fieldName)).map(Optional::of).orElse(imports.getField(fieldName))
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
        return new ClassType(getSuperClassName()).getInternalName();
    }

    public Type getClassType() {
        String className = getFullClassName();
        return new ClassType(className);
    }

    public String getClassInternalName() {
        return getClassType().getInternalName();
    }

    private ImportResolver getImports() {
        return imports;
    }

    public ClassType resolveClassName(String className) {
        return imports.getClass(className).orElse(new ClassType(className));
    }

}