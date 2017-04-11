package com.kubadziworski.domain.types;

/**
 * Created by plozano on 4/10/2017.
 */
public class RField implements RVariable {

    private final String name;
    private final RType owner;
    private final TypeReference type;
    private final Modifiers modifiers;

    public RField(String name, RType owner, TypeReference type, Modifiers modifiers) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.modifiers = modifiers;
    }

    public RType getOwner() {
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
