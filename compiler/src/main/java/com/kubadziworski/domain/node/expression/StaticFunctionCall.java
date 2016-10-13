package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;


import java.util.Collections;
import java.util.List;


public class StaticFunctionCall implements Call {

    private final FunctionSignature signature;
    private final List<Argument> arguments;
    private final Type type;
    private final Type owner;

    public StaticFunctionCall(FunctionSignature signature, List<Argument> arguments, Type owner) {
        this.signature = signature;
        this.arguments = arguments;
        this.type = signature.getReturnType();
        this.owner = owner;
    }

    @Override
    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return signature.getName();
    }

    @Override
    public Type getType() {
        return type;
    }

    public Type getOwner() {
        return owner;
    }

    public FunctionSignature getSignature() {
        return signature;
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
