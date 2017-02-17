package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 02.04.16.
 */
public class Value extends ElementImpl implements Expression {

    private final Object value;
    private final Type type;

    public Value(Type type, Object value) {
        this(null, type, value);
    }

    public Value(NodeData element, Type type, Object value) {
        super(element);
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }


    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Value{" +
                "value='" + value + '\'' +
                ", type=" + type +
                "} " + super.toString();
    }
}
