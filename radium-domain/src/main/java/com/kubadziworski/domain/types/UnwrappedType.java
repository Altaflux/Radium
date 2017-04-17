package com.kubadziworski.domain.types;

/**
 * Created by pablo.lozano on 4/16/2017.
 */
public abstract class UnwrappedType implements RType {

    public UnwrappedType unwrap() {
        return this;
    }
}
