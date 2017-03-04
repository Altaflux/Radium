package com.kubadziworski.domain.scope;

import com.google.common.collect.Ordering;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.ParameterForNameNotFoundException;
import com.kubadziworski.exception.WrongArgumentNameException;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignature implements CallableDescriptor {
    private final String name;
    private final List<Parameter> parameters;
    private final Type returnType;
    private final Modifiers modifiers;
    private final Type owner;
    private final SignatureType signatureType;

//    public FunctionSignature(String name, List<Parameter> parameters, Type returnType, Modifiers modifiers, Type owner) {
//        this.name = name;
//        this.parameters = parameters;
//        this.returnType = returnType;
//        this.modifiers = modifiers;
//        this.owner = owner;
//        this.signatureType = null;
//    }

    public FunctionSignature(String name, List<Parameter> parameters, Type returnType, Modifiers modifiers, Type owner, SignatureType signatureType) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.modifiers = modifiers;
        this.owner = owner;
        this.signatureType = signatureType;
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

    public int matches(String otherSignatureName, List<ArgumentHolder> arguments) {
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


    public List<Argument> createArgumentList(List<ArgumentHolder> arguments) {
        List<ArgumentHolder> sortedArguments = getSortedArguments(arguments, parameters);

        return IntStream.range(0, arguments.size()).mapToObj(i -> {
            ArgumentHolder argument = sortedArguments.get(i);
            Type parameterType = parameters.get(i).getType();
            return new Argument(argument.getExpression(), argument.getParameterName().orElse(null), parameterType);
        }).collect(Collectors.toList());
    }


    private List<ArgumentHolder> getSortedArguments(List<ArgumentHolder> arguments, List<Parameter> parameters) {
        Comparator<ArgumentHolder> argumentIndexComparator = (o1, o2) -> {
            if (!o1.getParameterName().isPresent()) return 0;
            return getIndexOfArgument(o1, parameters) - getIndexOfArgument(o2, parameters);
        };
        return Ordering.from(argumentIndexComparator).immutableSortedCopy(arguments);
    }

    private Integer getIndexOfArgument(ArgumentHolder argument, List<Parameter> parameters) {
        String paramName = argument.getParameterName().get();
        return parameters.stream()
                .filter(p -> p.getName().equals(paramName))
                .map(parameters::indexOf)
                .findFirst()
                .orElseThrow(() -> new WrongArgumentNameException(argument, parameters));
    }


    private int areArgumentsAndParamsMatchedByIndex(List<ArgumentHolder> arguments) {

        List<Parameter> nonDefault = parameters.stream()
                .filter(parameter -> !parameter.getDefaultValue().isPresent()).collect(Collectors.toList());
        if (arguments.size() == 0 && parameters.size() == 0) {
            return 0;
        }
        if (arguments.size() != parameters.size()) {
            if ((arguments.size() != nonDefault.size() && parameters.size() == 0)) {
                return -1;
            }
        }
        List<Integer> list = IntStream.range(0, arguments.size()).map(i -> {
            Type argumentType = arguments.get(i).getExpression().getType();
            Type parameterType = parameters.get(i).getType();
            return argumentType.inheritsFrom(parameterType);
        }).boxed().collect(Collectors.toList());

        if (list.contains(-1)) {
            return -1;
        } else {
            return list.stream().mapToInt(Integer::intValue).max().orElse(0);
        }
    }

    private boolean areArgumentsAndParamsMatchedByName(List<ArgumentHolder> arguments) {
        return arguments.stream().allMatch(a -> {
            String paramName = a.getParameterName().get();
            return parameters.stream()
                    .anyMatch(s -> s.getName().equals(paramName) || s.getDefaultValue().isPresent());
        });
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public Type getType() {
        return returnType;
    }


    public Modifiers getModifiers() {
        return modifiers;
    }

    public int getInvokeOpcode() {
        if (modifiers.contains(com.kubadziworski.domain.Modifier.STATIC)) {
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
