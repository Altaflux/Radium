package com.kubadziworski.domain.type.intrinsic;


import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AnyType implements Type {

    private final Type objectClass = new JavaClassType(java.lang.Object.class);
    private final List<FunctionSignature> functionSignatures;

    public static AnyType INSTANCE = new AnyType();

    private AnyType() {
        Modifiers modifiers = Modifiers.empty().with(Modifier.PUBLIC);
        Parameter parameter = new Parameter("other", this, null);
        FunctionSignature equalsSignature = new FunctionSignature("equals", Collections.singletonList(parameter),
                PrimitiveTypes.BOOLEAN_TYPE, modifiers, this, SignatureType.FUNCTION_CALL);
        FunctionSignature constructorSignature = new FunctionSignature("Any", Collections.emptyList(),
                this, modifiers, this, SignatureType.FUNCTION_CALL);
        FunctionSignature toString = new FunctionSignature("toString", Collections.emptyList(),
                DefaultTypes.STRING, modifiers, this, SignatureType.FUNCTION_CALL);
        FunctionSignature hashCode = new FunctionSignature("hashCode", Collections.emptyList(),
                PrimitiveTypes.INT_TYPE, modifiers, this, SignatureType.FUNCTION_CALL);

        functionSignatures = Arrays.asList(equalsSignature, constructorSignature, toString, hashCode);
    }

    @Override
    public String getName() {
        return "radium.Any";
    }

    @Override
    public Optional<Type> getSuperType() {
        return objectClass.getSuperType();
    }

    @Override
    public List<Field> getFields() {
        return Collections.emptyList();
    }

    @Override
    public List<FunctionSignature> getConstructorSignatures() {
        return objectClass.getConstructorSignatures();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return functionSignatures;
    }

    @Override
    public int inheritsFrom(Type type) {
        return objectClass.inheritsFrom(type);
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        return objectClass.nearestDenominator(type);
    }

    @Override
    public boolean isPrimitive() {
        return objectClass.isPrimitive();
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return objectClass.getAsmType();
    }

    @Override
    public Nullability isNullable() {
        return Nullability.NOT_NULL;
    }

}
