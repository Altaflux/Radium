package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;

public class EmptyExpression extends ElementImpl implements Expression {

    private final Type type;

    public EmptyExpression(Type type) {
        this(null, type);
    }

    public EmptyExpression(NodeData element, Type type) {
        super(element);
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
