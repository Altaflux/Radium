package com.kubadziworski.domain.types.builder;

public interface MemberBuilder<T, Owner> {

    T build(Owner owner);
}
