package com.kubadziworski.domain.expression;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 14.04.16.
 */
public class EmptyExpression extends Expression {

    public EmptyExpression(Type type) {
        super(type);
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }
}
