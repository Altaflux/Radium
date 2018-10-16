package com.kubadziworski.util;

import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;

import java.util.Collections;

public class CommonFunctionSignatures {

    public static final FunctionSignature equalsSignature = new FunctionSignature("equals", Collections.singletonList(new Parameter("other", AnyType.INSTANCE, null)),
            PrimitiveTypes.BOOLEAN_TYPE, Modifiers.empty().with(Modifier.PUBLIC), AnyType.INSTANCE, SignatureType.FUNCTION_CALL);

    public static final FunctionSignature toString = new FunctionSignature("toString", Collections.emptyList(),
            DefaultTypes.STRING, Modifiers.empty().with(Modifier.PUBLIC), AnyType.INSTANCE, SignatureType.FUNCTION_CALL);

}
