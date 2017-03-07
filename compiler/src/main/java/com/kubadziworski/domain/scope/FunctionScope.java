package com.kubadziworski.domain.scope;


import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.LocalVariableNotFoundException;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FunctionScope {

    private final Scope scope;
    private final FunctionSignature currentFunction;
    private final LinkedMap<String, LocalVariable> localVariables;
    private final Map<String, Field> fields;

    public FunctionScope(Scope scope, FunctionSignature currentFunction) {
        this.scope = scope;
        this.currentFunction = currentFunction;
        localVariables = new LinkedMap<>();
        fields = new LinkedMap<>();
    }

    public FunctionScope(FunctionScope functionScope) {
        this.scope = functionScope.scope;
        this.currentFunction = functionScope.currentFunction;
        localVariables = new LinkedMap<>(functionScope.localVariables);
        fields = functionScope.fields;
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

    public void addLocalVariable(LocalVariable variable) {
        localVariables.put(variable.getName(), variable);
    }

    public LocalVariable getLocalVariable(String varName) {
        return Optional.ofNullable(localVariables.get(varName))
                .orElseThrow(() -> new LocalVariableNotFoundException(this, varName));
    }

    public FunctionSignature getMethodCallSignature(String identifier, List<ArgumentHolder> arguments) {
        return scope.getMethodCallSignature(identifier, arguments);
    }

    public Scope getScope() {
        return scope;
    }

    public void addField(String name, Field field) {
        fields.put(name, field);
    }

    public boolean isFieldExists(String name) {
        return fields.containsKey(name) || scope.isFieldExists(name);
    }

    public FunctionSignature getCurrentFunctionSignature() {
        return currentFunction;
    }

    public Field getField(String fieldName) {
        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }
        return scope.getField(fieldName);
    }


    public String getFullClassName() {
        return scope.getFullClassName();
    }

    public Type resolveClassName(String className) {
        return scope.resolveClassName(className);
    }

    public Type getClassType() {
        return scope.getClassType();
    }

    public Type getSuperClassType() {
        return scope.getSuperClassType();
    }
}
