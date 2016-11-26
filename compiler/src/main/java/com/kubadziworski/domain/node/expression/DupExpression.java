package com.kubadziworski.domain.node.expression;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.type.Type;

public class DupExpression extends ElementImpl implements Expression {

    private final Expression expression;
    private final int dupShift;

    public DupExpression(Expression expression, int dupShift) {
        this.expression = expression;
        this.dupShift = dupShift;
    }

    public Expression getExpression() {
        return expression;
    }

    public int getDupShift() {
        return dupShift;
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
