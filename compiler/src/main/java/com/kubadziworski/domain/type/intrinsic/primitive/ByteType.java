package com.kubadziworski.domain.type.intrinsic.primitive;

import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;

import java.util.List;


public class ByteType extends AbstractPrimitiveType {

    private static final Type NULLABLE_TYPE = new JavaClassType(java.lang.Byte.class);
    private static final Type PRIMITIVE_TYPE = BuiltInType.BYTE;
    private static final Type physicalType = new JavaClassType(radium.Byte.class);

    public ByteType(boolean primitive) {
        super(primitive ? PRIMITIVE_TYPE : NULLABLE_TYPE, primitive, physicalType);
    }

    @Override
    public String getName() {
        return "radium.Byte";
    }

    @Override
    public Type getBoxedType() {
        return new ByteType(false);
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return physicalType.getFunctionSignatures();
    }
    @Override
    public Type getUnBoxedType() {
        return new ByteType(true);
    }

    public Value primitiveDummyValue() {
        return new Value(PRIMITIVE_TYPE, (byte) 0);
    }
}