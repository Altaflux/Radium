package com.kubadziworski.antlr.domain.expression;

import com.kubadziworski.antlr.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public class Value extends Expression {

    private String value;

    public Value(Type type, String value) {
        super(type);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
