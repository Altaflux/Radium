package com.kubadziworski.antlr.domain.scope;

import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.type.Type;

/**
 * Created by kuba on 09.04.16.
 */
public class LocalVariable extends Expression {
    private String name;

    public LocalVariable(String name, Type type) {
        super(type);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
