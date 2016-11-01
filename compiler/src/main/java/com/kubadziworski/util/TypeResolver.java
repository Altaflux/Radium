package com.kubadziworski.util;

import com.google.common.primitives.*;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.TypeContext;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
public final class TypeResolver {

    public static Type getFromTypeContext(TypeContext typeContext) {
        if (typeContext == null) return BultInType.VOID;
        return getFromTypeName(typeContext.getText());
    }

    public static Type getFromTypeContext(TypeContext typeContext, Scope scope) {
        if (typeContext == null) return BultInType.VOID;
        String typeName = typeContext.getText();

        if (typeName.equals("java.lang.String")) return DefaultTypes.STRING;
        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if (builtInType.isPresent()) return builtInType.get();
        return scope.resolveClassName(typeName);
    }

    public static Type getFromTypeName(String typeName) {
        if (typeName.equals("java.lang.String")) return DefaultTypes.STRING;
        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if (builtInType.isPresent()) return builtInType.get();
        return new JavaClassType(typeName);
    }

    public static Type getFromValue(EnkelParser.ValueContext value) {
        String stringValue = value.getText();
        if (StringUtils.isEmpty(stringValue)) return BultInType.VOID;

        if (value.IntegerLiteral() != null) {
            stringValue = stringValue.replace("_", "");
            if (stringValue.startsWith("0x") || stringValue.startsWith("0X") || stringValue.startsWith("0")) {
                if (tryInteger(stringValue) != null) {
                    return BultInType.INT;
                }
            }

            if (stringValue.endsWith("l") || stringValue.endsWith("L")) {
                return BultInType.LONG;
            }
            if (Ints.tryParse(stringValue) != null) {
                return BultInType.INT;
            } else if (Longs.tryParse(stringValue) != null) {
                return BultInType.LONG;
            }
        } else if (value.FloatingPointLiteral() != null) {
            stringValue = stringValue.replace("_", "");

            if (stringValue.startsWith("0x") || stringValue.startsWith("0X") || stringValue.startsWith("0")) {
                if (tryLongHex(stringValue) != null) {
                    return BultInType.LONG;
                }
            }

            if (stringValue.endsWith("f") || stringValue.endsWith("F")) {
                return BultInType.FLOAT;
            }
            if (Doubles.tryParse(stringValue) != null) {
                return BultInType.DOUBLE;
            } else if (Floats.tryParse(stringValue) != null) {
                return BultInType.FLOAT;
            }

        } else if (value.BOOL() != null) {
            return BultInType.BOOLEAN;
        }

        if (value.CharacterLiteral() != null) {
            return BultInType.CHAR;
        }

        return DefaultTypes.STRING;
    }


    public static Object getValueFromString(String stringValue, Type type) {
        if (TypeChecker.isInt(type)) {
            if (stringValue.startsWith("-")) {
                String newValue = stringValue.substring(1);
                return -UnsignedInts.decode(newValue);
            } else {
                return UnsignedInts.decode(stringValue);
            }
        }
        if (TypeChecker.isLong(type)) {
            if (stringValue.startsWith("-")) {
                String newValue = stringValue.substring(1);
                return -Long.decode(newValue);
            } else {
                return UnsignedLongs.decode(stringValue);
            }
        }

        if (TypeChecker.isFloat(type)) {
            return Float.valueOf(stringValue);
        }
        if (TypeChecker.isDouble(type)) {
            return Double.valueOf(stringValue);
        }
        if (TypeChecker.isBool(type)) {
            return Boolean.valueOf(stringValue);
        }
        if (type == BultInType.CHAR) {
            stringValue = StringUtils.removeStart(stringValue, "'");
            stringValue = StringUtils.removeEnd(stringValue, "'");
            return stringValue;
        }
        if (type == DefaultTypes.STRING) {
            stringValue = StringUtils.removeStart(stringValue, "\"");
            stringValue = StringUtils.removeEnd(stringValue, "\"");
            return stringValue;
        }
        throw new AssertionError("Objects not yet implemented!");
    }

    private static Integer tryInteger(String stringValue) {
        try {
            return UnsignedInts.decode(stringValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long tryLongHex(String stringValue) {
        try {
            return UnsignedLongs.decode(stringValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Optional<BultInType> getBuiltInType(String typeName) {
        return Arrays.stream(BultInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }
}
