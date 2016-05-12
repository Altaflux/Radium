package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.type.Type;
import lombok.ToString;

import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
@ToString
public class Parameter implements Expression {
    private final String name;
    private final Optional<Expression> defaultValue;
    private final Type type;

    public Parameter(String name, Type type, Optional<Expression> defaultValue) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public Optional<Expression> getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void accept(ExpressionGenerator genrator) {
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


}
