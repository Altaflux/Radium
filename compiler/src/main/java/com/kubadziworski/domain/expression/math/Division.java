package com.kubadziworski.domain.expression.math;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.expression.Expression;

/**
 * Created by kuba on 10.04.16.
 */
public class Division extends ArthimeticExpression {
    public Division(Expression leftExpress, Expression rightExpress) {
        super(leftExpress,rightExpress);
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }
}
