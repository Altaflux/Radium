package com.kubadziworski.antlr.domain.expression;

import com.kubadziworski.antlr.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public class FunctionParameter extends Expression {
    private final String name;

    public FunctionParameter(String name, Type type) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
