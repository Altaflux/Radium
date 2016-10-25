package com.kubadziworski.domain.node.expression;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;

public class PropertyAccessorCall extends FunctionCall {

    private final boolean isGetter;
    private final Field field;

    public PropertyAccessorCall(FunctionSignature signature, Argument argument, Expression owner, Field field) {
        super(signature, Collections.singletonList(argument), owner);
        this.isGetter = false;
        this.field = field;
    }

    public PropertyAccessorCall(FunctionSignature signature, Expression ownerType, Field field) {
        super(signature, Collections.emptyList(), ownerType);
        this.isGetter = true;
        this.field = field;
    }

    public boolean isGetter() {
        return isGetter;
    }

    public Field getField() {
        return field;
    }
}
