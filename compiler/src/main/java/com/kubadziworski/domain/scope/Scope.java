package com.kubadziworski.domain.scope;

import com.google.common.collect.Lists;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.MetaData;
import com.kubadziworski.domain.node.expression.Call;
import com.kubadziworski.domain.node.expression.SuperCall;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.LocalVariableNotFoundException;
import com.kubadziworski.exception.MethodSignatureNotFoundException;
import com.kubadziworski.exception.MethodWithNameAlreadyDefinedException;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by kuba on 02.04.16.
 */
public class Scope {
    private final List<LocalVariable> localVariables;
    private final List<FunctionSignature> functionSignatures;
    private final MetaData metaData;

    public Scope(MetaData metaData) {
        localVariables = new ArrayList<>();
        functionSignatures = new ArrayList<>();
        this.metaData = metaData;
    }

    public Scope(Scope scope) {
        metaData = scope.metaData;
        localVariables =  Lists.newArrayList(scope.localVariables);
        functionSignatures = Lists.newArrayList(scope.functionSignatures);
    }

    public void addSignature(FunctionSignature signature) {
        if(isParameterLessSignatureExists(signature.getName())) {
            throw new MethodWithNameAlreadyDefinedException(signature);
        }
        functionSignatures.add(signature);
    }

    public boolean isParameterLessSignatureExists(String identifier) {
        return isSignatureExists(identifier,Collections.emptyList());
    }

    public boolean isSignatureExists(String identifier, List<Argument> arguments) {
        if(identifier.equals("super")) return true;
        return functionSignatures.stream()
                .anyMatch(signature -> signature.matches(identifier,arguments));
    }

    public FunctionSignature getMethodCallSignatureWithoutParameters(String identifier) {
        return getMethodCallSignature(identifier, Collections.<Argument>emptyList());
    }

    public FunctionSignature getConstructorCallSignature(String className,List<Argument> arguments) {
        boolean isDifferentThanCurrentClass = !className.equals(getClassName());
        if(isDifferentThanCurrentClass) {
            List<Type> argumentsTypes = arguments.stream().map(Argument::getType).collect(toList());
            return new ClassPathScope().getConstructorSignature(className, argumentsTypes)
                    .orElseThrow(() -> new MethodSignatureNotFoundException(this,className,arguments));
        }
        return getConstructorCallSignatureForCurrentClass(arguments);
    }

    private FunctionSignature getConstructorCallSignatureForCurrentClass(List<Argument> arguments) {
        return getMethodCallSignature(Optional.empty(), getClassName(), arguments);
    }

    public FunctionSignature getMethodCallSignature(Optional<Type> owner,String methodName,List<Argument> arguments) {
        boolean isDifferentThanCurrentClass = owner.isPresent() && !owner.get().equals(getClassType());
        if(isDifferentThanCurrentClass) {
            List<Type> argumentsTypes = arguments.stream().map(Argument::getType).collect(toList());
            return new ClassPathScope().getMethodSignature(owner.get(), methodName, argumentsTypes)
                    .orElseThrow(() -> new MethodSignatureNotFoundException(this,methodName,arguments));
        }
        return getMethodCallSignature(methodName, arguments);
    }

    public FunctionSignature getMethodCallSignature(String identifier,List<Argument> arguments) {
        if(identifier.equals("super")){
            return new FunctionSignature("super", Collections.emptyList(), BultInType.VOID);
        }
        return functionSignatures.stream()
                .filter(signature -> signature.matches(identifier,arguments))
                .findFirst()
                .orElseThrow(() -> new MethodSignatureNotFoundException(this, identifier,arguments));
    }

    private String getSuperClassName() {
        return metaData.getSuperClassName();
    }

    public void addLocalVariable(LocalVariable localVariable) {
        localVariables.add(localVariable);
    }

    public LocalVariable getLocalVariable(String varName) {
        return localVariables.stream()
                .filter(variable -> variable.getName().equals(varName))
                .findFirst()
                .orElseThrow(() -> new LocalVariableNotFoundException(this, varName));
    }

    public boolean isLocalVariableExists(String varName) {
        return localVariables.stream()
                .anyMatch(variable -> variable.getName().equals(varName));
    }

    public int getLocalVariableIndex(String varName) {
        LocalVariable localVariable = getLocalVariable(varName);
        return localVariables.indexOf(localVariable);
    }

    public String getClassName() {
        return metaData.getClassName();
    }

    public String getSuperClassInternalName() {
        return new ClassType(getSuperClassName()).getInternalName();
    }

    public Type getClassType() {
        String className = getClassName();
        return new ClassType(className);
    }

    public String getClassInternalName() {
        return getClassType().getInternalName();
    }

}