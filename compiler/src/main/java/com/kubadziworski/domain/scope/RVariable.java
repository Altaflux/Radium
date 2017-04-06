package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.type.rtype.TypeReference;

/**
 * Created by plozano on 4/5/2017.
 */
public interface RVariable {

    TypeReference getType();
    String getName();
}
