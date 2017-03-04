package com.kubadziworski.domain.type.intrinsic.override;

import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import org.apache.commons.collections4.ListUtils;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class JavaInteger extends JavaClassType {

    private final List<FunctionSignature> functionSignatures;

    public JavaInteger() {
        super(java.lang.Integer.class);

        Modifiers modifier = Modifiers.empty().with(Modifier.PUBLIC);
        Parameter parameter = new Parameter("o", PrimitiveTypes.INT_TYPE, null);

        FunctionSignature compareTo = new FunctionSignature("compareTo",
                Collections.singletonList(parameter), PrimitiveTypes.INT_TYPE, modifier, this, SignatureType.FUNCTION_CALL);

        FunctionSignature intValue = new FunctionSignature("intValue",
                Collections.emptyList(), PrimitiveTypes.INT_TYPE, modifier, this, SignatureType.FUNCTION_CALL);
        FunctionSignature byteValue = new FunctionSignature("byteValue",
                Collections.emptyList(), PrimitiveTypes.BYTE_TYPE, modifier, this, SignatureType.FUNCTION_CALL);

        FunctionSignature doubleValue = new FunctionSignature("doubleValue",
                Collections.emptyList(), PrimitiveTypes.DOUBLE_TYPE, modifier, this, SignatureType.FUNCTION_CALL);

        FunctionSignature shortValue = new FunctionSignature("shortValue",
                Collections.emptyList(), PrimitiveTypes.SHORT_TYPE, modifier, this, SignatureType.FUNCTION_CALL);

        FunctionSignature floatValue = new FunctionSignature("floatValue",
                Collections.emptyList(), PrimitiveTypes.FLOAT_TYPE, modifier, this, SignatureType.FUNCTION_CALL);

        FunctionSignature longValue = new FunctionSignature("longValue",
                Collections.emptyList(), PrimitiveTypes.LONG_TYPE, modifier, this, SignatureType.FUNCTION_CALL);

        functionSignatures = ListUtils.sum(AnyType.INSTANCE.getFunctionSignatures(),
                Arrays.asList(compareTo, intValue, byteValue, doubleValue, shortValue, floatValue, longValue));
    }


    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return functionSignatures;
    }
}
