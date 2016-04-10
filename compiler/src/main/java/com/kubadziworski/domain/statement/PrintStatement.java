package com.kubadziworski.domain.statement;

import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.expression.Expression;

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


    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
