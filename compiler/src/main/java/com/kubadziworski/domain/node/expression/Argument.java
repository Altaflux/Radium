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
    private final boolean defaultValue;

    public Argument(Expression expression, String parameterName, Type receiverType) {
        this.parameterName = parameterName;
        this.expression = expression;
        this.receiverType = receiverType;
        this.defaultValue = false;
    }

    public Argument(Expression expression, String parameterName, Type receiverType, boolean defaultValue) {
        this.parameterName = parameterName;
        this.expression = expression;
        this.receiverType = receiverType;
        this.defaultValue = defaultValue;
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

    public boolean isDefaultValue() {
        return defaultValue;
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
