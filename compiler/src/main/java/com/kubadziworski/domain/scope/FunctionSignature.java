package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.ParameterForNameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignature {
    private final String name;
    private final List<Parameter> parameters;
    private final Type returnType;

    public FunctionSignature(String name, List<Parameter> parameters, Type returnType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public Parameter getParameterForName(String name) {
        return parameters.stream()
                .filter(param -> param.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ParameterForNameNotFoundException(name,parameters));
    }

    public int getIndexOfParameter(String parameterName) {
        Parameter parameter = getParameterForName(parameterName);
        return parameters.indexOf(parameter);
    }

    public boolean matches(String otherSignatureName, List<Argument> arguments) {
        boolean namesAreEqual = this.name.equals(otherSignatureName);
        if(!namesAreEqual) return false;
        long nonDefaultParametersCount = parameters.stream()
                .filter(p -> !p.getDefaultValue().isPresent())
                .count();
        if(nonDefaultParametersCount > arguments.size()) return false;
        boolean isNamedArgList = arguments.stream().anyMatch(a -> a.getParameterName().isPresent());
        if(isNamedArgList) {
            return areArgumentsAndParamsMatchedByName(arguments);
        }
        return areArgumentsAndParamsMatchedByIndex(arguments);
    }

    private boolean areArgumentsAndParamsMatchedByIndex(List<Argument> arguments) {
        return IntStream.range(0, arguments.size())
                .allMatch(i -> {
                    Type argumentType = arguments.get(i).getType();
                    Type parameterType = parameters.get(i).getType();
                    return argumentType.equals(parameterType);
                });
    }

    private boolean areArgumentsAndParamsMatchedByName(List<Argument> arguments) {
        return arguments.stream().allMatch(a -> {
            String paramName = a.getParameterName().get();
            return parameters.stream()
                    .map(Parameter::getName)
                    .anyMatch(paramName::equals);
        });
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FunctionSignature that = (FunctionSignature) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        return !(returnType != null ? !returnType.equals(that.returnType) : that.returnType != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FunctionSignature{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                ", returnType=" + returnType +
                '}';
    }
}
