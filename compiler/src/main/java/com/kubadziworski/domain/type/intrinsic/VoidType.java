package com.kubadziworski.domain.type.intrinsic;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class VoidType implements Type {

    private static final org.objectweb.asm.Type VOID = org.objectweb.asm.Type.VOID_TYPE;

    public static final VoidType INSTANCE = new VoidType();

    private VoidType() {
    }

    @Override
    public String getName() {
        return "void";
    }

    @Override
    public Optional<Type> getSuperType() {
        return Optional.empty();
    }

    @Override
    public List<Field> getFields() {
        return Collections.emptyList();
    }

    @Override
    public List<FunctionSignature> getConstructorSignatures() {
        return Collections.emptyList();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return Collections.emptyList();
    }

    @Override
    public int inheritsFrom(Type type) {
        if (type.getAsmType().equals(VOID)) {
            return 0;
        }
        if (type.equals(UnitType.CONCRETE_INSTANCE)) {
            return 0;
        }
        return -1;
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        if (type.getAsmType().equals(VOID)) {
            return Optional.of(type);
        }
        return Optional.empty();
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public Nullability isNullable() {
        return Nullability.NOT_NULL;
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return VOID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        return o instanceof Type && getName().equals(((Type) o).getName());
    }


}
