package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;

public interface CallableDescriptor {

    Type getType();
    Type getOwner();
    String getName();

    int getModifiers();

    default List<Parameter> getParameters() {
        return Collections.emptyList();
    }
}
