package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 02.04.16.
 */
public class FunctionCall implements Call {
    private Expression owner;
    private FunctionSignature signature;
    private List<Expression> arguments;
    private Type type;

    public FunctionCall(FunctionSignature signature, List<Expression> arguments, Expression owner) {
        this.type = signature.getReturnType();
        this.signature = signature;
        this.arguments = arguments;
        this.owner = owner;
    }

    public FunctionCall(FunctionSignature signature, List<Expression> arguments, Type ownerType) {
        this(signature,arguments,new EmptyExpression(ownerType));
    }

    public List<Expression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return signature.getName();
    }

    public Type getOwnerType() {
        return owner.getType();
    }

    public Expression getOwner() {
        return owner;
    }

    public FunctionSignature getSignature() {
        return signature;
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    @Override
    public Type getType() {
        return type;
    }
}
