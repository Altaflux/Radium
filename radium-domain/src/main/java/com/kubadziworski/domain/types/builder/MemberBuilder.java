package com.kubadziworski.domain.types.builder;

import com.kubadziworski.domain.types.RType;


public interface MemberBuilder<T> {

    T build(RType owner);
}
