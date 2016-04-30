package com.kubadziworski.util;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.TypeChecker;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.routines.IntegerValidator;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
public class TypeResolver {
    public static Type getFromTypeName(EnkelParser.TypeContext typeContext) {
        if(typeContext == null) return BultInType.VOID;
        String typeName = typeContext.getText();
        if(typeName.equals("java.lang.String")) return BultInType.STRING;
        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if(builtInType.isPresent()) return builtInType.get();
        return new ClassType(typeName);
    }

    public static Type getFromValue(String value) {
        if(StringUtils.isEmpty(value)) return BultInType.VOID;
        if(NumberUtils.isNumber(value)) {
            if (Ints.tryParse(value) != null) {
                return BultInType.INT;
            } else if(Floats.tryParse(value) != null) {
                return BultInType.FLOAT;
            } else if(Doubles.tryParse(value) != null) {
                return BultInType.DOUBLE;
            }
        } else if (BooleanUtils.toBoolean(value)) {
            return BultInType.BOOLEAN;
        }
        return BultInType.STRING;
    }

    public static Object getValueFromString(String stringValue, Type type) {
        if (TypeChecker.isInt(type)) {
            return Integer.valueOf(stringValue);
        } else if (TypeChecker.isFloat(type)) {
            return Float.valueOf(stringValue);
        } else if (TypeChecker.isDouble(type)) {
            return Double.valueOf(stringValue);
        } else if (TypeChecker.isBool(type)) {
            return Boolean.valueOf(stringValue);
        }
        else if (type == BultInType.STRING) {
            stringValue = StringUtils.removeStart(stringValue, "\"");
            stringValue = StringUtils.removeEnd(stringValue, "\"");
            return stringValue;
        } else {
            throw new AssertionError("Objects not yet implemented!");
        }
    }

    private static Optional<BultInType> getBuiltInType(String typeName) {
        return Arrays.stream(BultInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }
}
