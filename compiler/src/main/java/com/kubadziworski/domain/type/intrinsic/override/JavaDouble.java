package com.kubadziworski.domain.type.intrinsic.override;

import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import org.apache.commons.collections4.ListUtils;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class JavaDouble extends JavaClassType {

    private final List<FunctionSignature> functionSignatures;

    public JavaDouble() {
        super("java.lang.Double");

        Parameter parameter = new Parameter("o", PrimitiveTypes.DOUBLE_TYPE, null);

        FunctionSignature compareTo = new FunctionSignature("compareTo",
                Collections.singletonList(parameter), PrimitiveTypes.INT_TYPE, Modifier.PUBLIC, this);

        FunctionSignature intValue = new FunctionSignature("intValue",
                Collections.emptyList(), PrimitiveTypes.INT_TYPE, Modifier.PUBLIC, this);
        FunctionSignature byteValue = new FunctionSignature("byteValue",
                Collections.emptyList(), PrimitiveTypes.BYTE_TYPE, Modifier.PUBLIC, this);

        FunctionSignature doubleValue = new FunctionSignature("doubleValue",
                Collections.emptyList(), PrimitiveTypes.DOUBLE_TYPE, Modifier.PUBLIC, this);

        FunctionSignature shortValue = new FunctionSignature("shortValue",
                Collections.emptyList(), PrimitiveTypes.SHORT_TYPE, Modifier.PUBLIC, this);

        FunctionSignature floatValue = new FunctionSignature("floatValue",
                Collections.emptyList(), PrimitiveTypes.FLOAT_TYPE, Modifier.PUBLIC, this);

        FunctionSignature longValue = new FunctionSignature("longValue",
                Collections.emptyList(), PrimitiveTypes.LONG_TYPE, Modifier.PUBLIC, this);

        functionSignatures = ListUtils.sum(AnyType.INSTANCE.getFunctionSignatures(),
                Arrays.asList(compareTo, intValue, byteValue, doubleValue, shortValue, floatValue, longValue));
    }


    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return functionSignatures;
    }
}
