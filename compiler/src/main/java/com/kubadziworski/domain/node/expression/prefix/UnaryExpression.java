package com.kubadziworski.domain.node.expression.prefix;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.UnarySign;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;


public class UnaryExpression implements Expression {

    private final UnarySign unarySign;
    private final Expression expression;

    public UnaryExpression(UnarySign unarySign, Expression expression) {
        this.unarySign = unarySign;
        this.expression = expression;
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
        generator.generate(this);
    }

    public UnarySign getUnarySign() {
        return unarySign;
    }

    public Expression getExpression() {
        return expression;
    }
}
