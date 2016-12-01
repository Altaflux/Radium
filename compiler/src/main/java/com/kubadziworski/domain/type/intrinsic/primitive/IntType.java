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

public class IntType extends AbstractPrimitiveType {

    private static final Type NULLABLE_TYPE = new JavaClassType(java.lang.Integer.class);
    private static final Type PRIMITIVE_TYPE = BuiltInType.INT;

    private static final Type physicalType = new JavaClassType(radium.Int.class);

    public final boolean isPrimitive;

    public IntType(boolean primitive) {
        super(primitive ? PRIMITIVE_TYPE : NULLABLE_TYPE, primitive);
        isPrimitive = primitive;
    }

    @Override
    public String getName() {
        return "radium.Int";
    }

    @Override
    public List<Field> getFields() {
        return physicalType.getFields();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {

//        FunctionSignature toString = new FunctionSignature("toString", Collections.emptyList(), this, Modifier.PUBLIC + Modifier.STATIC, NULLABLE_TYPE);
//        FunctionSignature hashCode = new FunctionSignature("hashCode", Collections.emptyList(), this, Modifier.PUBLIC + Modifier.STATIC, NULLABLE_TYPE);
//
        return physicalType.getFunctionSignatures();
    }

    @Override
    public Type getBoxedType() {
        return new IntType(false);
    }

    @Override
    public Type getUnBoxedType() {
        return new IntType(true);
    }

    @Override
    public void compare(CompareSign compareSign, MethodVisitor methodVisitor) {
        PrimitiveFunction.compareIntType(compareSign, methodVisitor);
    }

}
