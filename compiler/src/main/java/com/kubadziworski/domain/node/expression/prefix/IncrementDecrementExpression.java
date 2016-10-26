package com.kubadziworski.domain.node.expression.prefix;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Reference;
import com.kubadziworski.domain.type.Type;


public class IncrementDecrementExpression implements Expression {

    private final Reference reference;
    private final boolean prefix;
    private final ArithmeticOperator operator;

    public IncrementDecrementExpression(Reference reference, boolean prefix, ArithmeticOperator operator) {
        this.reference = reference;
        this.prefix = prefix;
        this.operator = operator;
    }

    public Reference getReference() {
        return reference;
    }

    public boolean isPrefix() {
        return prefix;
    }

    public ArithmeticOperator getOperator() {
        return operator;
    }

    @Override
    public Type getType() {
        return reference.getType();
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
