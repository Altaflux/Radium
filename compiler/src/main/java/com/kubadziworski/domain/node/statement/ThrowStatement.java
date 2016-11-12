package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;


public class ThrowStatement implements Statement {
    private final Expression expression;

    public ThrowStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean isReturnComplete() {
        return true;
    }
}
