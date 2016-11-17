package com.kubadziworski.domain.node.expression;

import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.RdElement;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.Collections;

public class PropertyAccessorCall extends FunctionCall {

    private final boolean isGetter;
    private final Field field;

    public PropertyAccessorCall(FunctionSignature signature, Argument argument, Expression owner, Field field) {
        this(null, signature, argument, owner, field);
    }

    public PropertyAccessorCall(NodeData element, FunctionSignature signature, Argument argument, Expression owner, Field field) {
        super(element, signature, Collections.singletonList(argument), owner);
        this.isGetter = false;
        this.field = field;
    }

    public PropertyAccessorCall(FunctionSignature signature, Expression ownerType, Field field) {
        this(null, signature, ownerType, field);
    }

    public PropertyAccessorCall(NodeData element, FunctionSignature signature, Expression ownerType, Field field) {
        super(element, signature, Collections.emptyList(), ownerType);
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
