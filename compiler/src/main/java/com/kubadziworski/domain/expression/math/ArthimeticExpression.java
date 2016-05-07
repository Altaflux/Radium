package com.kubadziworski.domain.expression.math;

import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 10.04.16.
 */
public abstract class ArthimeticExpression implements Expression {

    private Expression leftExpression;
    private Expression rightExpression;
    private Type type;

    public ArthimeticExpression(Expression leftExpression,Expression rightExpression) {
        this.type = getCommonType(leftExpression,rightExpression);
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

    @Override
    public Type getType() {
        return type;
    }
}
