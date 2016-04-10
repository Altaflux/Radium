package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.type.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
public class FunctionCall extends Expression implements Statement {
    private Type owner;
    private FunctionSignature signature;
    private Collection<Expression> parameters;

    public FunctionCall(FunctionSignature signature, Collection<Expression> parameters, Type owner) {
        super(signature.getReturnType());
        this.signature = signature;
        this.parameters = parameters;
        this.owner = owner;
    }

    public String getFunctionName() {
        return signature.getName();
    }

    public Collection<Expression> getParameters() {
        return Collections.unmodifiableCollection(parameters);
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
