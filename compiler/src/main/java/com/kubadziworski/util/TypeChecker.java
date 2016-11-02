package com.kubadziworski.util;

import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 30.04.16.
 */
public final class TypeChecker {
    public static boolean isInt(Type type) {
        return type == BuiltInType.INT;
    }

    public static boolean isBool(Type type) {
        return type == BuiltInType.BOOLEAN;
    }

    public static boolean isFloat(Type type) {
        return type == BuiltInType.FLOAT;
    }

    public static boolean isLong(Type type) {
        return type == BuiltInType.LONG;
    }

    public static boolean isDouble(Type type) {
        return type == BuiltInType.DOUBLE;
    }

    public static boolean isNumber(Type type) {
        return (type == BuiltInType.INT || type == BuiltInType.LONG || type == BuiltInType.DOUBLE || type == BuiltInType.FLOAT);

    }
}
