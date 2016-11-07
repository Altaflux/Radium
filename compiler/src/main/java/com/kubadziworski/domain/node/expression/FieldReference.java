package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 09.04.16.
 */
public class FieldReference implements Reference {

    private final Field field;
    private final Expression owner;

    public FieldReference(Field field, Expression owner) {
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

    public void acceptDup(StatementGenerator generator){
        generator.generateDup(this);
    }

    public String getOwnerInternalName() {
        return field.getOwnerInternalName();
    }

    public Expression getOwner() {
        return owner;
    }

    public Field getField(){
        return field;
    }
}
