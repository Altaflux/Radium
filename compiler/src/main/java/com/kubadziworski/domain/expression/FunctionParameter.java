package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.type.Type;

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

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }
}
