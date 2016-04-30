package com.kubadziworski.domain.expression.math;

import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.math3.analysis.function.Exp;

/**
 * Created by kuba on 10.04.16.
 */
public abstract class ArthimeticExpression extends Expression {

    private Expression leftExpression;
    private Expression rightExpression;

    public ArthimeticExpression(Expression leftExpression,Expression rightExpression) {
        super(getCommonType(leftExpression,rightExpression));
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    private static Type getCommonType(Expression leftExpression, Expression rightExpression) {
        if(rightExpression.getType() == BultInType.STRING) return BultInType.STRING;
        return leftExpression.getType();
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }
}
