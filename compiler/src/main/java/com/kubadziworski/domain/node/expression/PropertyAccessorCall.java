package com.kubadziworski.domain.node.expression;

import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.Collections;
import java.util.List;

public class PropertyAccessorCall extends FunctionCall {

    private final boolean isGetter;
    private final Field field;

    public PropertyAccessorCall(FunctionSignature signature, List<Argument> arguments, Expression owner, Field field) {
        this(null, signature, arguments, owner, field);
    }

    public PropertyAccessorCall(NodeData element, FunctionSignature signature, List<Argument> arguments, Expression owner, Field field) {
        super(element, signature, arguments, owner);
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
