package com.kubadziworski.bytecodegenerator.domain;

import com.kubadziworski.antlr.domain.expression.Identifier;
import com.kubadziworski.antlr.domain.expression.Value;
import com.kubadziworski.antlr.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public class Variable {
    private final Value value;
    private final String name;
    private final int index;

    public Variable(Value value, String name, int index) {
        this.value = value;
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public Value getValue() {
        return value;
    }

    public Type getType() {
        return value.getType();
    }
}
