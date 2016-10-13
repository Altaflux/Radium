package com.kubadziworski.domain.scope;

import com.kubadziworski.bytecodegeneration.FieldGenerator;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 13.05.16.
 */
public class Field implements Variable {

    private final String name;
    private final Type owner;
    private final Type type;
    private final int modifiers;

    public Field(String name, Type owner, Type type, int modifiers) {
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.modifiers = modifiers;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getModifiers() {
        return modifiers;
    }

    public String getOwnerInternalName() {
        return owner.getInternalName();
    }

    public void accept(FieldGenerator generator) {
        generator.generate(this);
    }
}
