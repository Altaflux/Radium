package com.kubadziworski.domain.type.intrinsic;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public final class NullType implements Type {

    public static NullType INSTANCE = new NullType();

    private NullType() {

    }

    @Override
    public String getName() {
        return "null";
    }

    @Override
    public String getDescriptor() {
        return null;
    }

    @Override
    public String getInternalName() {
        return null;
    }

    @Override
    public Optional<Type> getSuperType() {
        return Optional.empty();
    }

    public List<Field> getFields() {
        return Collections.emptyList();
    }

    @Override
    public List<FunctionSignature> getConstructorSignatures() {
        return Collections.emptyList();
    }

    public List<FunctionSignature> getFunctionSignatures() {
        return Collections.emptyList();
    }

    @Override
    public int inheritsFrom(Type type) {
        if (type.isNullable().equals(Nullability.NOT_NULL)) {
            return -1;
        }
        return 0;
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        return Optional.empty();
    }


    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        throw new UnsupportedOperationException("Cannot get ASM Type of NULL instance");
    }

    @Override
    public Nullability isNullable() {
        return Nullability.NULLABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof TypeProjection) {
            o = ((TypeProjection) o).getInternalType();
        }
        return o instanceof Type && getName().equals(((Type) o).getName());
    }

}
