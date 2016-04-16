package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.type.Type;

import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
public class FunctionParameter extends Expression {
    private final String name;
    private final Optional<Expression> defaultValue;

    public FunctionParameter(String name, Type type, Optional<Expression> defaultValue) {
        super(type);
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
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }
}
