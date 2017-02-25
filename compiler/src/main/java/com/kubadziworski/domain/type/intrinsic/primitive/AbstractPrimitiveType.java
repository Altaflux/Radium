package com.kubadziworski.domain.type.intrinsic.primitive;

import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BoxableType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.AnyType;

import java.util.List;
import java.util.Optional;


public abstract class AbstractPrimitiveType implements Type, BoxableType {

    protected final Type type;
    protected final Type mainType;
    private final boolean isBoxed;

    AbstractPrimitiveType(Type type, boolean primitive, Type mainType) {
        this.type = type;
        this.isBoxed = !primitive;
        this.mainType = mainType;
    }

    @Override
    public Optional<Type> getSuperType() {
        return Optional.of(AnyType.INSTANCE);
    }

    @Override
    public List<Field> getFields() {
        return type.getFields();
    }

    @Override
    public List<FunctionSignature> getConstructorSignatures() {
        return type.getConstructorSignatures();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return type.getFunctionSignatures();
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }

    @Override
    public Nullability isNullable() {
        return Nullability.NOT_NULL;
    }

    @Override
    public boolean isBoxed() {
        return isBoxed;
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return type.getAsmType();
    }

    @Override
    public int inheritsFrom(Type type) {
        if (this.getName().equals(type.getName())) {
            return 0;
        }
        return this.mainType.inheritsFrom(type);
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        if (this.getName().equals(type.getName())) {
            return Optional.of(type);
        }
        return this.mainType.nearestDenominator(type);
    }


    @Override
    public String toString() {
        return "AbstractPrimitiveType{" +
                "name=" + getName() +
                ", type=" + type +
                ", isBoxed=" + isBoxed +
                '}';
    }

    public abstract Value primitiveDummyValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;


        if (o instanceof AbstractPrimitiveType) {
            if (((AbstractPrimitiveType) o).isBoxed() != isBoxed()) {
                return false;
            }
        }

        return o instanceof Type && getName().equals(((Type) o).getName());
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + (isBoxed ? 1 : 0);
        return result;
    }
}
