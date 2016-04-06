package com.kubadziworski.antlr.domain.expression;

import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public class FunctionParameter extends Expression {
    private final String name;
    private final int index;

    public FunctionParameter(String name, Type type, int index) {
        super(type);
        this.name = name;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
}
