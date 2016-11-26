package com.kubadziworski.domain.type.intrinsic;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BoxableType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.IncompatibleTypesException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class TypeProjection implements Type {

    private final Nullability nullable;
    private final Type type;

    public TypeProjection(Type type, Nullability nullable) {
        this.nullable = nullable;

        if(type instanceof TypeProjection){
            type = ((TypeProjection) type).getInternalType();
        }

        if (type instanceof BoxableType && !((BoxableType) type).isBoxed() && nullable.equals(Nullability.NULLABLE)) {
            this.type = ((BoxableType) type).getBoxedType();
        } else {
            this.type = type;
        }

        if (type.equals(UnitType.INSTANCE) && nullable.equals(Nullability.NULLABLE)) {
            throw new IncompatibleTypesException("Cannot set Unit to Nullable type", this, type);
        }
    }

    @Override
    public String getName() {
        return type.getName();
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
        if (nullable.equals(Nullability.NULLABLE)) {
            return Collections.emptyList();
        }
        return type.getFields();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        if (nullable.equals(Nullability.NULLABLE)) {
            return Collections.emptyList();
        }
        return type.getFunctionSignatures();
    }

    @Override
    public int inheritsFrom(Type type) {
        if (this.isNullable().equals(Nullability.NULLABLE) && type.isNullable().equals(Nullability.NOT_NULL)) {
            return -1;
        }

        return this.type.inheritsFrom(type);
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        return this.type.nearestDenominator(type);
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
        return type.isPrimitive();
    }

    @Override
    public Nullability isNullable() {
        return nullable;
    }


    public Type getInternalType() {
        return type;
    }


    @Override
    public String toString() {
        return "TypeProjection{" +
                "nullable=" + nullable +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof Type ) {
            if (nullable != ((Type) o).isNullable()) return false;
        }

        if(o instanceof TypeProjection){
            o = ((TypeProjection) o).getInternalType();
        }

        return type.equals(o);

    }

    @Override
    public int hashCode() {
        int result = nullable.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
