package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.IncompatibleTypesException;
import lombok.ToString;

import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
@ToString
public class Parameter extends ElementImpl implements Expression {
    private final String name;
    private final Expression defaultValue;
    private final Type type;
    private final boolean visible;

    public Parameter(String name, Type type, Expression defaultValue) {
        this(name, type, defaultValue, true);
    }

    public Parameter(String name, Type type, Expression defaultValue, boolean visible) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        this.visible = visible;
        if (defaultValue != null) {
            validateType(defaultValue, type);
        }
    }

    public String getName() {
        return name;
    }

    public Optional<Expression> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }


    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    @Override
    public Type getType() {
        return type;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (defaultValue != null ? !defaultValue.equals(parameter.defaultValue) : parameter.defaultValue != null)
            return false;
        return !(type != null ? !type.equals(parameter.type) : parameter.type != null);

    }

    @Override
    public int hashCode() {
        int result = defaultValue != null ? defaultValue.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    private static void validateType(Expression expression, Type variable) {
        if (expression.getType().inheritsFrom(variable) < 0) {
            throw new IncompatibleTypesException(variable.getName(), variable, expression.getType());
        }
    }
}
