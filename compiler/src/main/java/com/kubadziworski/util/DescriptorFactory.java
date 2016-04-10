package com.kubadziworski.util;

import com.kubadziworski.domain.classs.Function;
import com.kubadziworski.domain.expression.FunctionParameter;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by kuba on 29.03.16.
 */

//According to https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.3
public class DescriptorFactory {

    public static String getMethodDescriptor(Function function) {
        Collection<FunctionParameter> arguments = function.getArguments();
        Type returnType = function.getReturnType();
        return getMethodDescriptor(arguments, returnType);
    }

    public static String getMethodDescriptor(FunctionSignature signature) {
        Collection<FunctionParameter> arguments = signature.getArguments();
        Type returnType = signature.getReturnType();
        return getMethodDescriptor(arguments, returnType);
    }

    private static String getMethodDescriptor(Collection<FunctionParameter> arguments, Type returnType) {
        String argumentsDescriptor = arguments.stream()
                .map(argument -> argument.getType().getDescriptor())
                .collect(Collectors.joining("", "(", ")"));
        String returnDescriptor = returnType.getDescriptor();
        return argumentsDescriptor + returnDescriptor;
    }
}
