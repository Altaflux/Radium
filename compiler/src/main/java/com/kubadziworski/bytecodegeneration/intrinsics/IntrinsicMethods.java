package com.kubadziworski.bytecodegeneration.intrinsics;


import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

public class IntrinsicMethods {

    private final IntrinsicMap intrinsicMap = new IntrinsicMap();
    private final PrimitiveCoercion primitiveCoercion = new PrimitiveCoercion();
    private final ToString toString = new ToString();
    private final CompareTo compareTo = new CompareTo();
    private final Equals equals = new Equals();
    private final PrimitiveComparison primitiveComparison = new PrimitiveComparison();

    private final ArithmeticIntrinsicMethod arithmeticIntrinsicMethod = new ArithmeticIntrinsicMethod();

    public IntrinsicMethods() {

        PrimitiveTypes.NUMBER_TYPES.forEach(type -> {
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toInt", Collections.emptyList()),
                            primitiveCoercion, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toLong", Collections.emptyList()),
                            primitiveCoercion, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toChar", Collections.emptyList()),
                            primitiveCoercion, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toShort", Collections.emptyList()),
                            primitiveCoercion, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toDouble", Collections.emptyList()),
                            primitiveCoercion, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toFloat", Collections.emptyList()),
                            primitiveCoercion, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toByte", Collections.emptyList()),
                            primitiveCoercion, 0);

        });

        PrimitiveTypes.PRIMITIVE_TYPES.forEach(type -> {
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("toString", Collections.emptyList()),
                            toString, type);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("equals", Collections.singletonList(
                            new ArgumentHolder((AnyType.INSTANCE), null))),
                            equals, type);


            Modifiers modifiers = Modifiers.empty().with(com.kubadziworski.domain.Modifier.PUBLIC);
            FunctionSignature primitiveEquals = new FunctionSignature("==", Collections.singletonList(new Parameter("o", type, null)),
                    PrimitiveTypes.BOOLEAN_TYPE, modifiers, type);
            intrinsicMap.registerIntrinsicMethod(primitiveEquals, primitiveComparison, type);
            FunctionSignature primitiveNotEquals = new FunctionSignature("!=", Collections.singletonList(new Parameter("o", type, null)),
                    PrimitiveTypes.BOOLEAN_TYPE, modifiers, type);
            intrinsicMap.registerIntrinsicMethod(primitiveNotEquals, primitiveComparison, type);

        });


        PrimitiveTypes.NUMERIC_TYPES.forEach(type -> PrimitiveTypes.NUMERIC_TYPES.forEach(type1 -> {
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("plus", Collections.singletonList(
                            new ArgumentHolder((type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("minus", Collections.singletonList(
                            new ArgumentHolder((type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("div", Collections.singletonList(
                            new ArgumentHolder((type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("mod", Collections.singletonList(
                            new ArgumentHolder((type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("times", Collections.singletonList(
                            new ArgumentHolder((type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("plus", Collections.singletonList(
                            new ArgumentHolder((type1), null))),
                            arithmeticIntrinsicMethod, 0);

            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("compareTo", Collections.singletonList(
                            new ArgumentHolder((type1), null))),
                            compareTo, 0);


            Stream.of(CompareSign.values()).forEach(compareSign -> {
                if (!compareSign.equals(CompareSign.EQUAL) && !compareSign.equals(CompareSign.NOT_EQUAL)) {
                    FunctionSignature primitiveCompare = new FunctionSignature(compareSign.getSign(), Collections.singletonList(new Parameter("o", type1, null)),
                            PrimitiveTypes.BOOLEAN_TYPE, Modifiers.empty().with(com.kubadziworski.domain.Modifier.PUBLIC), type);
                    intrinsicMap.registerIntrinsicMethod(primitiveCompare, primitiveComparison, type);

                }

            });

        }));

        intrinsicMap
                .registerIntrinsicMethod(PrimitiveTypes.BOOLEAN_TYPE.getMethodCallSignature("compareTo", Collections.singletonList(
                        new ArgumentHolder((PrimitiveTypes.BOOLEAN_TYPE), null))),
                        compareTo, 0);
        intrinsicMap
                .registerIntrinsicMethod(PrimitiveTypes.CHAR_TYPE.getMethodCallSignature("compareTo", Collections.singletonList(
                        new ArgumentHolder((PrimitiveTypes.CHAR_TYPE), null))),
                        compareTo, 0);

    }


    public Optional<IntrinsicMethod> intrinsicMethod(CallableMember member) {
        return intrinsicMap.getIntrinsicMethod(member);
    }
}
