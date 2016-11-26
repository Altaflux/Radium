package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.type.Type;

import java.util.Optional;

/**
 * Created by kuba on 09.05.16.
 */
public class Argument extends ElementImpl implements Expression {

    private final String parameterName;
    private final Expression expression;
    private final Type receiverType;

    public Argument(Expression expression, String parameterName) {
        this(expression, parameterName, null);
    }

    public Argument(Expression expression, String parameterName, Type receiverType) {
        this.parameterName = parameterName;
        this.expression = expression;
        this.receiverType = receiverType;
    }

    @Override
    public Type getType() {
        return expression.getType();
    }

    public Type getReceiverType() {
        return receiverType;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Optional<String> getParameterName() {
        return Optional.ofNullable(parameterName);
    }

    @Override
    public String toString() {
        return "Argument{" +
                "parameterName=" + parameterName +
                ", descriptor='" + getType() + '\'' +
                '}';
    }
}
