package com.kubadziworski.exception;

import com.kubadziworski.domain.node.expression.Expression;

/**
 * Created by kuba on 23.04.16.
 */
public class UnsupportedRangedLoopTypes extends RuntimeException {
    public UnsupportedRangedLoopTypes(Expression startExpression, Expression endExpression) {
        super("Only integer types are supported so far");
    }
}
