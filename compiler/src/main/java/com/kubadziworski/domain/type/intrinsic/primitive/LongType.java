package com.kubadziworski.domain.type.intrinsic.primitive;

import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.function.PrimitiveFunction;
import org.objectweb.asm.MethodVisitor;

import java.util.List;


public class LongType extends AbstractPrimitiveType {

    private static final Type NULLABLE_TYPE = new JavaClassType("java.lang.Long");
    private static final Type PRIMITIVE_TYPE = BuiltInType.LONG;

    private static final Type physicalType = new JavaClassType("radium.Long");

    public LongType(boolean primitive) {
        super(primitive ? PRIMITIVE_TYPE : NULLABLE_TYPE, primitive);
    }

    @Override
    public String getName() {
        return "radium.Long";
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
        return new LongType(false);
    }

    @Override
    public Type getUnBoxedType() {
        return new LongType(true);
    }


    public void compare(CompareSign compareSign, MethodVisitor methodVisitor) {
        PrimitiveFunction.compareLong(compareSign, methodVisitor);
    }

}
