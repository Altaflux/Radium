package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 09.04.16.
 */
public class FieldReference extends ElementImpl implements Reference, CallableMember {

    private final Field field;
    private final Expression owner;

    public FieldReference(Field field, Expression owner) {
        this(null, field, owner);
    }

    public FieldReference(NodeData element, Field field, Expression owner) {
        super(element);
        this.field = field;
        this.owner = owner;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Type getType() {
        return field.getType();
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }


    public Expression getOwner() {
        return owner;
    }

    public Field getField() {
        return field;
    }
}
