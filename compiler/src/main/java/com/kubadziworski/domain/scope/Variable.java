package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 13.05.16.
 */
public interface Variable {
    Type getType();
    String getName();
}
