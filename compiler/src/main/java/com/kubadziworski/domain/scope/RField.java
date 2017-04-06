package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.type.rtype.TypeReference;

/**
 * Created by plozano on 4/5/2017.
 */
public class RField implements RVariable {

    private final String name;
    private final TypeReference owner;
    private final TypeReference type;
    private final Modifiers modifiers;

    public RField(String name, TypeReference owner, TypeReference type, Modifiers modifiers) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.modifiers = modifiers;
    }

    public TypeReference getOwner() {
        return owner;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public TypeReference getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }
}
