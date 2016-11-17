package com.kubadziworski.domain.node.expression;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.type.Type;

public class DupExpression extends ElementImpl implements Expression {

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
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
