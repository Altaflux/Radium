package com.kubadziworski.domain.type.intrinsic.primitive;


import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PrimitiveTypes {

    public static final Type BOOLEAN_TYPE = new BoolType(true);
    public static final Type INT_TYPE = new IntType(true);
    public static final Type LONG_TYPE = new LongType(true);
    public static final Type DOUBLE_TYPE = new DoubleType(true);
    public static final Type FLOAT_TYPE = new FloatType(true);
    public static final Type CHAR_TYPE = new CharType(true);
    public static final Type BYTE_TYPE = new ByteType(true);
    public static final Type SHORT_TYPE = new ShortType(true);


    public static final Type BOOLEAN_BOX_TYPE = new BoolType(false);
    public static final Type INT_BOX_TYPE = new IntType(false);
    public static final Type LONG_BOX_TYPE = new LongType(false);
    public static final Type DOUBLE_BOX_TYPE = new DoubleType(false);
    public static final Type FLOAT_BOX_TYPE = new FloatType(false);
    public static final Type CHAR_BOX_TYPE = new CharType(false);
    public static final Type BYTE_BOX_TYPE = new ByteType(false);
    public static final Type SHORT_BOX_TYPE = new ShortType(false);

    public static final List<Type> PRIMITIVE_TYPES = Arrays.asList(BOOLEAN_TYPE, INT_TYPE, LONG_TYPE, DOUBLE_TYPE, FLOAT_TYPE, BYTE_TYPE, CHAR_TYPE, SHORT_TYPE);


    public static AbstractPrimitiveType getBiggerDenominator(AbstractPrimitiveType type1, AbstractPrimitiveType type2) {
        if(type1.equals(BOOLEAN_TYPE) || type1.equals(BOOLEAN_BOX_TYPE)){
            return type1;
        }

        FunctionSignature signature1 = type1.getMethodCallSignature("plus", Collections.singletonList(new ArgumentHolder(new EmptyExpression(type2), null)));
        FunctionSignature signature2 = type2.getMethodCallSignature("plus", Collections.singletonList(new ArgumentHolder(new EmptyExpression(type1), null)));

        if (signature1.getReturnType().equals(signature2.getReturnType())) {
            return (AbstractPrimitiveType) signature1.getReturnType();
        }
        throw new RuntimeException("Could not match highest denominator for given primitives:" + type1.getName() + " ::: " + type2.getName());
    }


    private PrimitiveTypes() {
    }
}
