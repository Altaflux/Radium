package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 14.04.16.
 */
public class EmptyExpression implements Expression {

    private Type type;

    public EmptyExpression(Type type) {
        this.type = type;
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
