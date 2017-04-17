package com.kubadziworski.domain.types;


public abstract class WrappedType implements RType {

    protected abstract RType getDelegate();

    public UnwrappedType unwrap() {
        RType type = getDelegate();
        while (type instanceof WrappedType) {
            type = ((WrappedType) type).getDelegate();
        }
        return (UnwrappedType) type;
    }

}
