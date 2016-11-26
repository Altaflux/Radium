package com.kubadziworski.domain.type.intrinsic.primitive;

import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.function.PrimitiveFunction;
import org.objectweb.asm.MethodVisitor;


public class ByteType extends AbstractPrimitiveType {

    private static final Type NULLABLE_TYPE = new JavaClassType("java.lang.Byte");
    private static final Type PRIMITIVE_TYPE = BuiltInType.BYTE;

    public ByteType(boolean primitive) {
        super(primitive ? PRIMITIVE_TYPE : NULLABLE_TYPE, primitive);
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
    public Type getUnBoxedType() {
        return new ByteType(true);
    }

    @Override
    public void compare(CompareSign compareSign, MethodVisitor methodVisitor) {
        PrimitiveFunction.compareIntType(compareSign, methodVisitor);
    }
}