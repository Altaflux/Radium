package com.kubadziworski.domain.type.intrinsic.primitive;

import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BoxableType;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;

import java.util.List;
import java.util.Optional;


public abstract class AbstractPrimitiveType implements Type, BoxableType {

    protected final Type type;
    private final boolean isBoxed;

    AbstractPrimitiveType(Type type, boolean primitive) {
        this.type = type;
        this.isBoxed = !primitive;
    }

    @Override
    public Class<?> getTypeClass() {
        return type.getTypeClass();
    }

    @Override
    public String getDescriptor() {
        return type.getDescriptor();
    }

    @Override
    public String getInternalName() {
        return type.getInternalName();
    }

    @Override
    public Optional<Type> getSuperType() {
        return type.getSuperType();
    }

    @Override
    public List<Field> getFields() {
        return type.getFields();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        return type.getFunctionSignatures();
    }

    @Override
    public int getDupCode() {
        return type.getDupCode();
    }

    @Override
    public int getDupX1Code() {
        return type.getDupX1Code();
    }

    @Override
    public int getLoadVariableOpcode() {
        return type.getLoadVariableOpcode();
    }

    @Override
    public int getStoreVariableOpcode() {
        return type.getStoreVariableOpcode();
    }

    @Override
    public int getReturnOpcode() {
        return type.getReturnOpcode();
    }

    @Override
    public int getAddOpcode() {
        return type.getAddOpcode();
    }

    @Override
    public int getSubstractOpcode() {
        return type.getSubstractOpcode();
    }

    @Override
    public int getMultiplyOpcode() {
        return type.getMultiplyOpcode();
    }

    @Override
    public int getDividOpcode() {
        return type.getDividOpcode();
    }

    @Override
    public int getNegation() {
        return type.getNegation();
    }

    @Override
    public int getStackSize() {
        return type.getStackSize();
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
    public int inheritsFrom(Type type) {
        if (this.getName().equals(type.getName())) {
            return 0;
        }
        return this.type.inheritsFrom(type);
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        if (this.getName().equals(type.getName())) {
            return Optional.of(type);
        }
        return this.type.nearestDenominator(type);
    }

    public abstract void compare(CompareSign compareSign, MethodVisitor methodVisitor);

    @Override
    public String toString() {
        return "AbstractPrimitiveType{" +
                "name=" + getName() +
                ", type=" + type +
                ", isBoxed=" + isBoxed +
                '}';
    }

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
