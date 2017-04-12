package com.kubadziworski.domain.types;

import lombok.Builder;

public class UpperBoundConstraintImpl implements UpperBoundConstraint {

    private final TypeReference type;
    private final ConstraintOwner owner;

    @Builder
    public UpperBoundConstraintImpl(TypeReference type, ConstraintOwner owner) {
        this.type = type;
        this.owner = owner;
    }

    @Override
    public TypeReference getType() {
        return type;
    }

    @Override
    public ConstraintOwner getOwner() {
        return owner;
    }
}
