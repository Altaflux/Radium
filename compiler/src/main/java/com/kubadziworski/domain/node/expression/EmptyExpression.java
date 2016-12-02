package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 14.04.16.
 */
public class EmptyExpression extends ElementImpl implements Expression {

    private final Type type;

    public EmptyExpression(Type type) {
        this.type = type;
    }


    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    @Override
    public String toString() {
        return "EmptyExpression{" +
                "type=" + type +
                "} " + super.toString();
    }
}
