package com.kubadziworski.util;

import com.kubadziworski.antlr.EnkelParser.TypeContext;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.*;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.exception.AmbiguousCallException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes.PRIMITIVE_TYPES;


public final class TypeResolver {


    public static Type getFromTypeContext(TypeContext typeContext, FunctionScope scope) {
        return getFromTypeContext(typeContext, scope.getScope());
    }

    public static Type getFromTypeContext(TypeContext typeContext, Scope scope) {
        if (typeContext == null) return VoidType.INSTANCE;
        String typeName = typeContext.simpleName.getText();

        Type.Nullability nullability = typeContext.nullable != null ? Type.Nullability.NULLABLE : Type.Nullability.NOT_NULL;
        if (typeName.equals("java.lang.String")) return new TypeProjection(DefaultTypes.STRING, nullability);

        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if (builtInType.isPresent()) return builtInType.get();

        Type contextType = scope.resolveClassName(typeName);
        return new TypeProjection(contextType, nullability);
    }


    //For usage of ReflectionObjectToSignatureMapper
    static Type getTypeFromNameWithClazzAlias(Class clazz, Type.Nullability nullability) {
        String typeName = clazz.getCanonicalName();
        if (typeName.equals("void")) return VoidType.INSTANCE;
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
        if (typeName.equals("java.lang.Byte")) return new TypeProjection(PrimitiveTypes.BYTE_BOX_TYPE, nullability);
        if (typeName.equals("java.lang.Object")) return new TypeProjection(AnyType.INSTANCE, nullability);

        if (typeName.equals("java.lang.String")) return new TypeProjection(DefaultTypes.STRING, nullability);
        Optional<? extends Type> builtInType = getBuiltInType(typeName);
        if (builtInType.isPresent()) return builtInType.get();


        return new TypeProjection(ClassTypeFactory.createClassType(clazz), nullability);
    }


    private static Optional<Type> getBuiltInType(String typeName) {
        return PRIMITIVE_TYPES.stream()
                .filter(type -> type.getName().equals(typeName))
                .map(abstractPrimitiveType -> (Type) abstractPrimitiveType)
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
                throw new AmbiguousCallException(signatures);
            }
            return signatures.stream().reduce((functionSignature, functionSignature2) -> {
                boolean sameArguments = compareArguments(functionSignature.getParameters(), functionSignature2.getParameters());
                if (!sameArguments) {
                    throw new AmbiguousCallException(Arrays.asList(functionSignature, functionSignature2));
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
            return VoidType.INSTANCE;
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

