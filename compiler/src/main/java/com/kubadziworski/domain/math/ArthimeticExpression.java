package com.kubadziworski.domain.math;

import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.UnsupportedArthimeticOperationException;

/**
 * Created by kuba on 10.04.16.
 */
public abstract class ArthimeticExpression extends Expression {

    private Expression leftExpression;
    private Expression rightExpression;

    public ArthimeticExpression(Type type, Expression leftExpression, Expression rightExpression) {
        super(type);
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        if(type != BultInType.INT) {
            throw new UnsupportedArthimeticOperationException(this);
        }
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }
}
