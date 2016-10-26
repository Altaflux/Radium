package com.kubadziworski.util;

import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 30.04.16.
 */
public final class TypeChecker {
    public static boolean isInt(Type type) {
        return type == BultInType.INT;
    }

    public static boolean isBool(Type type) {
        return type == BultInType.BOOLEAN;
    }

    public static boolean isFloat(Type type) {
        return type == BultInType.FLOAT;
    }

    public static boolean isLong(Type type) {
        return type == BultInType.LONG;
    }

    public static boolean isDouble(Type type) {
        return type == BultInType.DOUBLE;
    }

    public static boolean isNumber(Type type) {
        return (type == BultInType.INT || type == BultInType.LONG || type == BultInType.DOUBLE || type == BultInType.FLOAT);

    }
}
