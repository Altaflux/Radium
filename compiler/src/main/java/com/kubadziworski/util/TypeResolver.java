package com.kubadziworski.util;

import com.google.common.primitives.*;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.TypeContext;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.*;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes.PRIMITIVE_TYPES;

/**
 * Created by kuba on 02.04.16.
 */
public final class TypeResolver {

    private static final Type BOOLEAN_TYPE = PrimitiveTypes.BOOLEAN_TYPE;
    private static final Type INT_TYPE = PrimitiveTypes.INT_TYPE;
    private static final Type LONG_TYPE = PrimitiveTypes.LONG_TYPE;
    private static final Type DOUBLE_TYPE = PrimitiveTypes.DOUBLE_TYPE;
    private static final Type FLOAT_TYPE = PrimitiveTypes.FLOAT_TYPE;
    private static final Type CHAR_TYPE = PrimitiveTypes.CHAR_TYPE;

    public static Type getFromTypeContext(TypeContext typeContext) {
        if (typeContext == null) return new TypeProjection(UnitType.INSTANCE, Type.Nullability.NOT_NULL);

        if (typeContext.nullable != null) {
            return getFromTypeName(typeContext.simpleName.getText(), Type.Nullability.NULLABLE);
        }
        return getFromTypeName(typeContext.simpleName.getText(), Type.Nullability.NOT_NULL);
    }

    public static Type getFromTypeContext(TypeContext typeContext, Scope scope) {
        if (typeContext == null) return UnitType.INSTANCE;
        String typeName = typeContext.simpleName.getText();

        Type.Nullability nullability = typeContext.nullable != null ? Type.Nullability.NULLABLE : Type.Nullability.NOT_NULL;

        if (typeName.equals("java.lang.String")) return new TypeProjection(DefaultTypes.STRING, nullability);

        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if (builtInType.isPresent()) return builtInType.get();

        Type contextType = scope.resolveClassName(typeName);
        //When parsing Radium classes we cannot use the Unit returned from the ClassTypeFactory as it will return the Concrete version
        if (contextType.getName().equals("radium.Unit") & nullability.equals(Type.Nullability.NOT_NULL)) {
            return new TypeProjection(UnitType.INSTANCE, nullability);
        }

        return new TypeProjection(contextType, nullability);
    }

    public static Type getFromTypeName(String typeName, Type.Nullability nullability) {

        if (typeName.equals("java.lang.String")) return new TypeProjection(DefaultTypes.STRING, nullability);

        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if (builtInType.isPresent()) return builtInType.get();
        return new TypeProjection(ClassTypeFactory.createClassType(typeName), nullability);
    }


    //For usage of ReflectionObjectToSignatureMapper
    public static Type getTypeFromNameWithClazzAlias(String typeName, Type.Nullability nullability) {
        if (typeName.equals("void")) return UnitType.INSTANCE;
        if (typeName.equals("boolean")) return new TypeProjection(PrimitiveTypes.BOOLEAN_TYPE, nullability);
        if (typeName.equals("int")) return new TypeProjection(PrimitiveTypes.INT_TYPE, nullability);
        if (typeName.equals("float")) return new TypeProjection(PrimitiveTypes.FLOAT_TYPE, nullability);
        if (typeName.equals("double")) return new TypeProjection(PrimitiveTypes.DOUBLE_TYPE, nullability);
        if (typeName.equals("char")) return new TypeProjection(PrimitiveTypes.CHAR_TYPE, nullability);
        if (typeName.equals("long")) return new TypeProjection(PrimitiveTypes.LONG_TYPE, nullability);
        if (typeName.equals("short")) return new TypeProjection(PrimitiveTypes.SHORT_TYPE, nullability);

        if (typeName.equals("java.lang.Boolean"))
            return new TypeProjection(PrimitiveTypes.BOOLEAN_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Integer")) return new TypeProjection(PrimitiveTypes.INT_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Float")) return new TypeProjection(PrimitiveTypes.FLOAT_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Double")) return new TypeProjection(PrimitiveTypes.DOUBLE_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Character"))
            return new TypeProjection(PrimitiveTypes.CHAR_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Long")) return new TypeProjection(PrimitiveTypes.LONG_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Short")) return new TypeProjection(PrimitiveTypes.SHORT_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Object")) return new TypeProjection(AnyType.INSTANCE, nullability);

        return getFromTypeName(typeName, nullability);
    }

    public static Type getFromValue(EnkelParser.ValueContext value) {
        String stringValue = value.getText();
        if (StringUtils.isEmpty(stringValue)) return UnitType.INSTANCE;

        if (stringValue.equals("null")) return NullType.INSTANCE;

        if (value.IntegerLiteral() != null) {
            stringValue = stringValue.replace("_", "");
            if (stringValue.startsWith("0x") || stringValue.startsWith("0X") || stringValue.startsWith("0")) {
                if (tryInteger(stringValue) != null) {
                    return INT_TYPE;
                }
            }

            if (stringValue.endsWith("l") || stringValue.endsWith("L")) {
                return LONG_TYPE;
            }
            if (Ints.tryParse(stringValue) != null) {
                return INT_TYPE;
            } else if (Longs.tryParse(stringValue) != null) {
                return LONG_TYPE;
            }
        } else if (value.FloatingPointLiteral() != null) {
            stringValue = stringValue.replace("_", "");

            if (stringValue.startsWith("0x") || stringValue.startsWith("0X") || stringValue.startsWith("0")) {
                if (tryLongHex(stringValue) != null) {
                    return LONG_TYPE;
                }
            }

            if (stringValue.endsWith("f") || stringValue.endsWith("F")) {
                return FLOAT_TYPE;
            }
            if (Doubles.tryParse(stringValue) != null) {
                return DOUBLE_TYPE;
            } else if (Floats.tryParse(stringValue) != null) {
                return FLOAT_TYPE;
            }

        } else if (value.BOOL() != null) {
            return BOOLEAN_TYPE;
        }

        if (value.CharacterLiteral() != null) {
            return CHAR_TYPE;
        }

        return DefaultTypes.STRING;
    }


    public static Object getValueFromString(String stringValue, Type type) {

        if (type.equals(NullType.INSTANCE)) return null;

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
        if (type == PrimitiveTypes.CHAR_TYPE) {
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

//    private static Optional<BuiltInType> getBuiltdInType(String typeName) {
//        return Arrays.stream(BuiltInType.values())
//                .filter(type -> type.getName().equals(typeName))
//                .findFirst();
//    }

    private static Optional<Type> getBuiltInType(String typeName) {
        return PRIMITIVE_TYPES.stream()
                .filter(type -> type.getName().equals(typeName))
                .findFirst().map(Optional::of).orElse((Arrays.stream(BuiltInType.values())
                        .filter(type -> type.getName().equals(typeName))
                        .map(type -> (Type) type)
                        .findFirst()));
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

    private static boolean compareArguments(List<Parameter> list1, List<Parameter> list2) {
        int result = IntStream.range(0, list1.size()).map(operand -> {
            if (list1.get(operand).getType().equals(list2.get(operand).getType())) {
                return 1;
            }
            return 0;
        }).min().orElse(1);
        return result == 1;
    }

    public static Type getCommonType(List<Type> types) {
        if (types.isEmpty()) {
            return UnitType.INSTANCE;
        }

        List<Type> commonTypesList = types.stream()
                .filter(type -> !type.equals(RadiumBuiltIns.NOTHING_TYPE))
                .collect(Collectors.toList());
        if (commonTypesList.isEmpty()) {
            return RadiumBuiltIns.NOTHING_TYPE;
        }
        return commonTypesList.stream()
                .reduce((type, type2) -> type.nearestDenominator(type2).orElse(AnyType.INSTANCE))
                .orElse(AnyType.INSTANCE);

    }
}

