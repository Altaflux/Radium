package com.kubadziworski.domain.types.builder;

import com.kubadziworski.domain.types.RType;


public interface MemberBuilder<T, Owner extends RType> {

    T build(Owner owner);
}
