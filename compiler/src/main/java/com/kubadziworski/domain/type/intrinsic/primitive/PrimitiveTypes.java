package com.kubadziworski.domain.type.intrinsic.primitive;


import com.kubadziworski.domain.type.Type;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PrimitiveTypes {

    public static final AbstractPrimitiveType BOOLEAN_TYPE = new BoolType(true);
    public static final AbstractPrimitiveType INT_TYPE = new IntType(true);
    public static final AbstractPrimitiveType LONG_TYPE = new LongType(true);
    public static final AbstractPrimitiveType DOUBLE_TYPE = new DoubleType(true);
    public static final AbstractPrimitiveType FLOAT_TYPE = new FloatType(true);
    public static final AbstractPrimitiveType CHAR_TYPE = new CharType(true);
    public static final AbstractPrimitiveType BYTE_TYPE = new ByteType(true);
    public static final AbstractPrimitiveType SHORT_TYPE = new ShortType(true);


    public static final AbstractPrimitiveType BOOLEAN_BOX_TYPE = new BoolType(false);
    public static final AbstractPrimitiveType INT_BOX_TYPE = new IntType(false);
    public static final AbstractPrimitiveType LONG_BOX_TYPE = new LongType(false);
    public static final AbstractPrimitiveType DOUBLE_BOX_TYPE = new DoubleType(false);
    public static final AbstractPrimitiveType FLOAT_BOX_TYPE = new FloatType(false);
    public static final AbstractPrimitiveType CHAR_BOX_TYPE = new CharType(false);
    public static final AbstractPrimitiveType BYTE_BOX_TYPE = new ByteType(false);
    public static final AbstractPrimitiveType SHORT_BOX_TYPE = new ShortType(false);

    public static final List<AbstractPrimitiveType> PRIMITIVE_TYPES = Arrays.asList(BOOLEAN_TYPE, CHAR_TYPE, INT_TYPE, LONG_TYPE, DOUBLE_TYPE, FLOAT_TYPE, BYTE_TYPE, SHORT_TYPE);
    public static final List<AbstractPrimitiveType> NUMERIC_TYPES = Arrays.asList(INT_TYPE, LONG_TYPE, DOUBLE_TYPE, FLOAT_TYPE, BYTE_TYPE, SHORT_TYPE);

    public static final List<AbstractPrimitiveType> NUMBER_TYPES = Arrays.asList(CHAR_TYPE, INT_TYPE, LONG_TYPE, DOUBLE_TYPE, FLOAT_TYPE, BYTE_TYPE, SHORT_TYPE);

    public static final LinkedList<Type> orderedDenominatorList = new LinkedList<>();

    static {
        orderedDenominatorList.add(BOOLEAN_TYPE);
        orderedDenominatorList.add(CHAR_TYPE);
        orderedDenominatorList.add(SHORT_TYPE);
        orderedDenominatorList.add(INT_TYPE);
        orderedDenominatorList.add(LONG_TYPE);
        orderedDenominatorList.add(FLOAT_TYPE);
        orderedDenominatorList.add(DOUBLE_TYPE);
    }


    public static AbstractPrimitiveType getBiggerDenominator(AbstractPrimitiveType type1, AbstractPrimitiveType type2) {
        int positionOfType1 = orderedDenominatorList.indexOf((AbstractPrimitiveType) type1.getUnBoxedType());
        int positionOfType2 = orderedDenominatorList.indexOf((AbstractPrimitiveType) type2.getUnBoxedType());
        return (AbstractPrimitiveType) orderedDenominatorList.get(Math.max(positionOfType1, positionOfType2));
    }

    public static Type getBiggerDenominator(AbstractPrimitiveType type1, Type type2) {

        if (type2.getAsmType().getSort() == org.objectweb.asm.Type.OBJECT) {
            return type2;
        }
        int positionOfType1 = orderedDenominatorList.indexOf((AbstractPrimitiveType) type1.getUnBoxedType());
        int positionOfType2 = orderedDenominatorList.indexOf(((AbstractPrimitiveType) type2).getUnBoxedType());
        return orderedDenominatorList.get(Math.max(positionOfType1, positionOfType2));
    }


    private PrimitiveTypes() {
    }
}
