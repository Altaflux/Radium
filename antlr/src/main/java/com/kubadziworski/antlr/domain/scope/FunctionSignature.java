package com.kubadziworski.antlr.domain.scope;

import com.kubadziworski.antlr.domain.expression.FunctionParameter;
import com.kubadziworski.antlr.domain.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignature {
    private final String name;
    private final List<FunctionParameter> arguments;
    private final Type returnType;

    public FunctionSignature(String name, List<FunctionParameter> arguments, Type returnType) {
        this.name = name;
        this.arguments = arguments;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public Type getReturnType() {
        return returnType;
    }
}
