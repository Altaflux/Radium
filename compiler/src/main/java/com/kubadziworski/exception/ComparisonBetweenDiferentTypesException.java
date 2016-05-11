package com.kubadziworski.exception;

import com.kubadziworski.domain.node.expression.Expression;

/**
 * Created by kuba on 12.04.16.
 */
public class ComparisonBetweenDiferentTypesException extends RuntimeException {
    public ComparisonBetweenDiferentTypesException(Expression leftExpression, Expression rightExpression) {
        super("Comparison between types " + leftExpression.getType() + " and " + rightExpression.getType() + " not yet supported");
    }
}
