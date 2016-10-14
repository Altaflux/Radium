package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.type.Type;


public class PopExpression implements Expression {

    private final Expression owner;

    public PopExpression(Expression owner) {
        this.owner = owner;
    }

    @Override
    public Type getType() {
        return owner.getType();
    }

    public Expression getOwner(){
        return owner;
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
