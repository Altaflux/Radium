package com.kubadziworski.domain.node.expression;


import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.type.Type;

public class DupExpression implements Expression {

    private final Expression expression;

    public DupExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Type getType() {
        return expression.getType();
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }

    @Override
    public void accept(StatementGenerator generator) {

    }
}
