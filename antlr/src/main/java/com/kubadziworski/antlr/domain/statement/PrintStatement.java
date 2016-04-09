package com.kubadziworski.antlr.domain.statement;

import com.kubadziworski.antlr.domain.expression.Expression;

/**
 * Created by kuba on 28.03.16.
 */
public class PrintStatement implements Statement {

    private Expression expression;

    public PrintStatement(Expression expression) {

        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
