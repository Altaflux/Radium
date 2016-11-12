package com.kubadziworski.util;

import com.google.common.primitives.*;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.TypeContext;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by kuba on 02.04.16.
 */
public final class TypeResolver {

    public static Type getFromTypeContext(TypeContext typeContext) {
        if (typeContext == null) return BuiltInType.VOID;
        return getFromTypeName(typeContext.getText());
    }

    public static Type getFromTypeContext(TypeContext typeContext, Scope scope) {
        if (typeContext == null) return BuiltInType.VOID;
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
        if (StringUtils.isEmpty(stringValue)) return BuiltInType.VOID;

        if(stringValue.equals("null")) return NullType.INSTANCE;

        if (value.IntegerLiteral() != null) {
            stringValue = stringValue.replace("_", "");
            if (stringValue.startsWith("0x") || stringValue.startsWith("0X") || stringValue.startsWith("0")) {
                if (tryInteger(stringValue) != null) {
                    return BuiltInType.INT;
                }
            }

            if (stringValue.endsWith("l") || stringValue.endsWith("L")) {
                return BuiltInType.LONG;
            }
            if (Ints.tryParse(stringValue) != null) {
                return BuiltInType.INT;
            } else if (Longs.tryParse(stringValue) != null) {
                return BuiltInType.LONG;
            }
        } else if (value.FloatingPointLiteral() != null) {
            stringValue = stringValue.replace("_", "");

            if (stringValue.startsWith("0x") || stringValue.startsWith("0X") || stringValue.startsWith("0")) {
                if (tryLongHex(stringValue) != null) {
                    return BuiltInType.LONG;
                }
            }

            if (stringValue.endsWith("f") || stringValue.endsWith("F")) {
                return BuiltInType.FLOAT;
            }
            if (Doubles.tryParse(stringValue) != null) {
                return BuiltInType.DOUBLE;
            } else if (Floats.tryParse(stringValue) != null) {
                return BuiltInType.FLOAT;
            }

        } else if (value.BOOL() != null) {
            return BuiltInType.BOOLEAN;
        }

        if (value.CharacterLiteral() != null) {
            return BuiltInType.CHAR;
        }

        return DefaultTypes.STRING;
    }


    public static Object getValueFromString(String stringValue, Type type) {

        if(type.equals(NullType.INSTANCE)) return null;

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
        if (type == BuiltInType.CHAR) {
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

    private static Optional<BuiltInType> getBuiltInType(String typeName) {
        return Arrays.stream(BuiltInType.values())
                .filter(type -> type.getName().equals(typeName))
                .findFirst();
    }

    public static Optional<FunctionSignature> resolveArity(Type owner, Map<Integer, List<FunctionSignature>> functions) {
        SortedMap<Integer, List<FunctionSignature>> maps = new TreeMap<>(functions);

        maps = maps.subMap(0, Integer.MAX_VALUE);
        if (maps.isEmpty()) {
            return Optional.empty();
        }
        List<FunctionSignature> signatures = maps.get(maps.firstKey());
        if (signatures == null) return Optional.empty();

        if (signatures.size() > 1) {
            if (owner == null) {
                throw new RuntimeException("Arity issue");
            }
            return signatures.stream().reduce((functionSignature, functionSignature2) -> {
                boolean sameArguments = compareArguments(functionSignature.getParameters(), functionSignature2.getParameters());
                if (!sameArguments) {
                    throw new RuntimeException("Arity issue");
                }
                int arity1 = owner.inheritsFrom(functionSignature.getOwner());
                int arity2 = owner.inheritsFrom(functionSignature2.getOwner());
                if (arity1 > arity2) {
                    return functionSignature;
                } else {
                    return functionSignature2;
                }
            });

        }
        return Optional.of(signatures.get(0));
    }

    public static boolean compareArguments(List<Parameter> list1, List<Parameter> list2) {
        int result = IntStream.range(0, list1.size()).map(operand -> {
            if (list1.get(operand).getType().equals(list2.get(operand).getType())) {
                return 1;
            }
            return 0;
        }).min().orElse(1);
        return result == 1;
    }
}

