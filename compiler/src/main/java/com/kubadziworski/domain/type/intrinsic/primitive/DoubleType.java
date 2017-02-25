package com.kubadziworski.domain.type.intrinsic.primitive;

import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;

import java.util.List;

public class DoubleType extends AbstractPrimitiveType {

    private static final Type NULLABLE_TYPE = new JavaClassType(java.lang.Double.class);
    private static final Type PRIMITIVE_TYPE = BuiltInType.DOUBLE;

    private static final Type physicalType = new JavaClassType(radium.Double.class);

    public DoubleType(boolean primitive) {
        super(primitive ? PRIMITIVE_TYPE : NULLABLE_TYPE, primitive, physicalType);
    }

    @Override
    public String getName() {
        return "radium.Double";
    }

    @Override
    public List<Field> getFields() {
        return physicalType.getFields();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return physicalType.getFunctionSignatures();
    }

    @Override
    public Type getBoxedType() {
        return new DoubleType(false);
    }

    @Override
    public Type getUnBoxedType() {
        return new DoubleType(true);
    }

    public Value primitiveDummyValue() {
        return new Value(PRIMITIVE_TYPE, 0D);
    }
}
