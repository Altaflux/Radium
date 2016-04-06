package com.kubadziworski.antlr.domain.expression;

import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.antlr.domain.statement.Statement;

/**
 * Created by kuba on 02.04.16.
 */
public abstract class Expression implements Statement {
    private Type type;

    public Expression(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
