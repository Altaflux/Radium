package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
public class FunctionCall extends Expression implements Statement {
    private Type owner;
    private FunctionSignature signature;
    private List<Expression> arguments;

    public FunctionCall(FunctionSignature signature, List<Expression> arguments, Type owner) {
        super(signature.getReturnType());
        this.signature = signature;
        this.arguments = arguments;
        this.owner = owner;
    }

    public String getFunctionName() {
        return signature.getName();
    }

    public List<Expression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public Optional<Type> getOwner() {
        return Optional.ofNullable(owner);
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
}
