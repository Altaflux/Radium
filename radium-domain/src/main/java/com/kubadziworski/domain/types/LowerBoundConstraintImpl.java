package com.kubadziworski.domain.types;


public class LowerBoundConstraintImpl extends ConstraintImpl implements LowerBoundConstraint {

    private final TypeReference type;

    public LowerBoundConstraintImpl(TypeReference type, ConstraintOwner owner) {
        super(owner);
        this.type = type;
    }

    @Override
    public TypeReference getType() {
        return type;
    }
}
