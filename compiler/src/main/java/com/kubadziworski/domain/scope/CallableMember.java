package com.kubadziworski.domain.scope;


import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;

public interface CallableMember {

    Expression getOwner();

    Type getType();

    String getName();

    default List<Argument> getArguments() {
        return Collections.emptyList();
    }

}
