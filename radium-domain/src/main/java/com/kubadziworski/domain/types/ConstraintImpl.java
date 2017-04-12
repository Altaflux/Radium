package com.kubadziworski.domain.types;


public abstract class ConstraintImpl implements Constraint {

    protected final ConstraintOwner owner;

    public ConstraintImpl(ConstraintOwner owner) {
        this.owner = owner;
    }

    public ConstraintOwner getOwner() {
        return owner;
    }
}
