package com.kubadziworski.domain.node.expression;

import com.kubadziworski.domain.type.Type;

import java.util.Optional;

public class ArgumentHolder {

    private final String parameterName;
    private final Expression expression;


    public ArgumentHolder(Expression expression, String parameterName) {
        this.parameterName = parameterName;
        this.expression = expression;
    }

    public ArgumentHolder(Type type, String parameterName) {
        this.parameterName = parameterName;
        this.expression = new EmptyExpression(type);
    }

    public Expression getExpression() {
        return expression;
    }

    public Optional<String> getParameterName() {
        return Optional.ofNullable(parameterName);
    }

    @Override
    public String toString() {
        return "ArgumentHolder{" +
                "parameterName='" + parameterName + '\'' +
                ", expression=" + expression +
                '}';
    }
}
