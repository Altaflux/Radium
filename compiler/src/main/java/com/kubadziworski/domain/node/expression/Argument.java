package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.type.Type;

import java.util.Optional;

/**
 * Created by kuba on 09.05.16.
 */
public class Argument implements Expression {

    private final String parameterName;
    private final Expression expression;

    public Argument(Expression expression, String parameterName) {
        this.parameterName = parameterName;
        this.expression = expression;
    }

    @Override
    public Type getType() {
        return expression.getType();
    }

    @Override
    public void accept(StatementGenerator generator) {
        expression.accept(generator);
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
