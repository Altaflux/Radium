package com.kubadziworski.domain.type.intrinsic;

import com.kubadziworski.bytecodegeneration.inline.CodeInliner;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.BoxableType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.CommonFunctionSignatures;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class TypeProjection implements Type {

    private final Nullability nullable;
    private final Type type;

    public TypeProjection(Type type, Nullability nullable) {
        this.nullable = nullable;

        if (type instanceof TypeProjection) {
            type = ((TypeProjection) type).getInternalType();
        }

        if (type instanceof BoxableType && !((BoxableType) type).isBoxed() && nullable.equals(Nullability.NULLABLE)) {
            this.type = ((BoxableType) type).getBoxedType();
        } else {
            this.type = type;
        }
    }

    @Override
    public String getName() {
        return type.getName();
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
    public List<FunctionSignature> getConstructorSignatures() {
        return type.getConstructorSignatures();
    }

    @Override
    public List<FunctionSignature> getFunctionSignatures() {
        if (nullable.equals(Nullability.NULLABLE)) {
            return Arrays.asList(CommonFunctionSignatures.equalsSignature, CommonFunctionSignatures.toString);
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
    public boolean isPrimitive() {
        return type.isPrimitive();
    }

    @Override
    public Nullability isNullable() {
        return nullable;
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return type.getAsmType();
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

    public CodeInliner getInliner() {
        return type.getInliner();
    }

    public List<Type> getInterfaces() {
        return type.getInterfaces();
    }

    public ClassType getClassType() {
        return type.getClassType();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (o instanceof Type) {
            if (nullable.equals(Nullability.NOT_NULL) && ((Type) o).isNullable().equals(Nullability.NULLABLE))
                return false;
        }

        if (o instanceof TypeProjection) {
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
