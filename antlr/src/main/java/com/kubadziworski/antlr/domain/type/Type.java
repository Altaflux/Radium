package com.kubadziworski.antlr.domain.type;

import java.util.Arrays;

/**
 * Created by kuba on 28.03.16.
 */
public interface Type {
    String getName();
    Class<?> getTypeClass();
    String getDescriptor();
    String getInternalName();
}
