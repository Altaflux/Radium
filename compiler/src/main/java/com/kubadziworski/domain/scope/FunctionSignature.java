package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.ParameterForNameNotFoundException;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignature {
    private final String name;
    private final List<Parameter> parameters;
    private final Type returnType;
    private final int modifiers;
    private final Type owner;

    public FunctionSignature(String name, List<Parameter> parameters, Type returnType, int modifiers, Type owner) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.owner = owner;
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
                .orElseThrow(() -> new ParameterForNameNotFoundException(name, parameters));
    }

    public int getIndexOfParameter(String parameterName) {
        Parameter parameter = getParameterForName(parameterName);
        return parameters.indexOf(parameter);
    }

    public int matches(String otherSignatureName, List<Argument> arguments) {
        boolean namesAreEqual = this.name.equals(otherSignatureName);
        if (!namesAreEqual) return -1;
        long nonDefaultParametersCount = parameters.stream()
                .filter(p -> !p.getDefaultValue().isPresent())
                .count();
        if (nonDefaultParametersCount > arguments.size()) return -1;
        boolean isNamedArgList = arguments.stream().anyMatch(a -> a.getParameterName().isPresent());
        if (isNamedArgList) {
            if (areArgumentsAndParamsMatchedByName(arguments)) return 0;
            else return -1;
        }
        return areArgumentsAndParamsMatchedByIndex(arguments);
    }

    public List<Argument> getMatchedParameters(List<Argument> arguments) {
        boolean isNamedArgList = arguments.stream().anyMatch(a -> a.getParameterName().isPresent());
        if (areArgumentsAndParamsMatchedByName(arguments)) {
            return arguments.stream().map(argument -> new Argument(argument.getExpression(), argument.getParameterName().orElse(null),
                    getParameterForName(argument.getParameterName().orElse(null)).getType())).collect(Collectors.toList());
        }

       return IntStream.range(0, arguments.size()).mapToObj(i -> {
            Argument argument = arguments.get(i);
            Type parameterType = parameters.get(i).getType();
            return new Argument(argument.getExpression(), argument.getParameterName().orElse(null), parameterType);
        }).collect(Collectors.toList());
    }

    private int areArgumentsAndParamsMatchedByIndex(List<Argument> arguments) {

        List<Parameter> nonDefault = parameters.stream()
                .filter(parameter -> !parameter.getDefaultValue().isPresent()).collect(Collectors.toList());
        if (arguments.size() == 0 && parameters.size() == 0) {
            return 0;
        }
        if (arguments.size() != parameters.size()) {
            if (arguments.size() != nonDefault.size()) {
                return -1;
            }
        }
        List<Integer> list = IntStream.range(0, arguments.size()).map(i -> {
            Type argumentType = arguments.get(i).getType();
            Type parameterType = parameters.get(i).getType();
            return argumentType.inheritsFrom(parameterType);
        }).boxed().collect(Collectors.toList());

        if (list.contains(-1)) {
            return -1;
        } else {
            return list.stream().mapToInt(Integer::intValue).max().orElse(0);
        }
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


    public int getModifiers() {
        return modifiers;
    }

    public int getInvokeOpcode() {
        if (Modifier.isStatic(modifiers)) {
            return Opcodes.INVOKESTATIC;
        } else {
            return Opcodes.INVOKEVIRTUAL;
        }
    }

    public Type getOwner() {
        return owner;
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
