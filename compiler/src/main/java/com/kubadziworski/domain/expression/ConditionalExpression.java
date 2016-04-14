package com.kubadziworski.domain.expression;

import com.kubadziworski.domain.global.CompareSign;
import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.type.BultInType;

/**
 * Created by kuba on 12.04.16.
 */
public class ConditionalExpression extends Expression {

    private CompareSign compareSign;
    private Expression leftExpression;
    private Expression rightExpression;

    public ConditionalExpression(Expression leftExpression, Expression rightExpression,CompareSign compareSign) {
        super(BultInType.BOOLEAN);
        this.compareSign = compareSign;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    public CompareSign getCompareSign() {
        return compareSign;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    public Expression getRightExpression() {
        return rightExpression;
    }

    @Override
    public void accept(ExpressionGenrator genrator) {
        genrator.generate(this);
    }
}

