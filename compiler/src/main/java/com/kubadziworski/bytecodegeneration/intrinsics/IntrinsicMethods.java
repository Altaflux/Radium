package com.kubadziworski.bytecodegeneration.intrinsics;


import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;

import java.util.Collections;
import java.util.Optional;

public class IntrinsicMethods {

    private final IntrinsicMap intrinsicMap = new IntrinsicMap();
    private final PrimitiveCoercion primitiveCoercion = new PrimitiveCoercion();
    private final ToString toString = new ToString();
    private final CompareTo compareTo = new CompareTo();
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

        PrimitiveTypes.PRIMITIVE_TYPES.forEach(type -> intrinsicMap
                .registerIntrinsicMethod(type.getMethodCallSignature("toString", Collections.emptyList()),
                        toString, type));


        PrimitiveTypes.NUMERIC_TYPES.forEach(type -> PrimitiveTypes.NUMERIC_TYPES.forEach(type1 -> {
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("plus", Collections.singletonList(
                            new ArgumentHolder(new EmptyExpression(type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("minus", Collections.singletonList(
                            new ArgumentHolder(new EmptyExpression(type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("div", Collections.singletonList(
                            new ArgumentHolder(new EmptyExpression(type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("mod", Collections.singletonList(
                            new ArgumentHolder(new EmptyExpression(type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("times", Collections.singletonList(
                            new ArgumentHolder(new EmptyExpression(type1), null))),
                            arithmeticIntrinsicMethod, 0);
            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("plus", Collections.singletonList(
                            new ArgumentHolder(new EmptyExpression(type1), null))),
                            arithmeticIntrinsicMethod, 0);

            intrinsicMap
                    .registerIntrinsicMethod(type.getMethodCallSignature("compareTo", Collections.singletonList(
                            new ArgumentHolder(new EmptyExpression(type1), null))),
                            compareTo, 0);
        }));

        intrinsicMap
                .registerIntrinsicMethod(PrimitiveTypes.BOOLEAN_TYPE.getMethodCallSignature("compareTo", Collections.singletonList(
                        new ArgumentHolder(new EmptyExpression(PrimitiveTypes.BOOLEAN_TYPE), null))),
                        compareTo, 0);
        intrinsicMap
                .registerIntrinsicMethod(PrimitiveTypes.CHAR_TYPE.getMethodCallSignature("compareTo", Collections.singletonList(
                        new ArgumentHolder(new EmptyExpression(PrimitiveTypes.CHAR_TYPE), null))),
                        compareTo, 0);

    }


    public Optional<IntrinsicMethod> intrinsicMethod(CallableMember member) {
        return intrinsicMap.getIntrinsicMethod(member);
    }
}
