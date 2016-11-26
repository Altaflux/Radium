package com.kubadziworski.util;

import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.*;


public final class TypeChecker {

    public static boolean isInt(Type type) {
        return type.inheritsFrom(new IntType(false)) == 0 && !type.isNullable().equals(Type.Nullability.NULLABLE);
    }

    public static boolean isBool(Type type) {
        return type.inheritsFrom(new BoolType(false)) == 0 && !type.isNullable().equals(Type.Nullability.NULLABLE);
    }

    public static boolean isFloat(Type type) {
        return type.inheritsFrom(new FloatType(false)) == 0 && !type.isNullable().equals(Type.Nullability.NULLABLE);
    }

    public static boolean isLong(Type type) {
        return type.inheritsFrom(new LongType(false)) == 0 && !type.isNullable().equals(Type.Nullability.NULLABLE);
    }

    public static boolean isDouble(Type type) {
        return type.inheritsFrom(new DoubleType(false)) == 0 && !type.isNullable().equals(Type.Nullability.NULLABLE);
    }

    public static boolean isNumber(Type type) {
        return (isInt(type) || isLong(type) || isDouble(type) || isFloat(type));
    }
}
