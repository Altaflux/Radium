package com.kubadziworski.domain.type;

import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by kuba on 02.04.16.
 */
public enum BuiltInType implements Type {

    BOOLEAN("boolean", boolean.class, "Z", TypeSpecificOpcodes.INT),
    INT("int", int.class, "I", TypeSpecificOpcodes.INT),
    CHAR("char", char.class, "C", TypeSpecificOpcodes.INT),
    BYTE("byte", byte.class, "B", TypeSpecificOpcodes.INT),
    SHORT("short", short.class, "S", TypeSpecificOpcodes.INT),
    LONG("long", long.class, "J", TypeSpecificOpcodes.LONG),
    FLOAT("float", float.class, "F", TypeSpecificOpcodes.FLOAT),
    DOUBLE("double", double.class, "D", TypeSpecificOpcodes.DOUBLE),
    BOOLEAN_ARR("bool[]", boolean[].class, "[B", TypeSpecificOpcodes.OBJECT),
    INT_ARR("int[]", int[].class, "[I", TypeSpecificOpcodes.OBJECT),
    CHAR_ARR("char[]", char[].class, "[C", TypeSpecificOpcodes.OBJECT),
    BYTE_ARR("byte[]", byte[].class, "[B", TypeSpecificOpcodes.OBJECT),
    SHORT_ARR("short[]", short[].class, "[S", TypeSpecificOpcodes.OBJECT),
    LONG_ARR("long[]", long[].class, "[J", TypeSpecificOpcodes.OBJECT),
    FLOAT_ARR("float[]", float[].class, "[F", TypeSpecificOpcodes.OBJECT),
    DOUBLE_ARR("double[]", double[].class, "[D", TypeSpecificOpcodes.OBJECT),
    NONE("", null, "", TypeSpecificOpcodes.OBJECT),
    STRING_ARR("string[]", String[].class, "[Ljava/lang/String;", TypeSpecificOpcodes.OBJECT);

    private final String name;
    private final Class<?> typeClass;
    private final String descriptor;
    private final TypeSpecificOpcodes opcodes;

    BuiltInType(String name, Class<?> typeClass, String descriptor, TypeSpecificOpcodes opcodes) {
        this.name = name;
        this.typeClass = typeClass;
        this.descriptor = descriptor;
        this.opcodes = opcodes;

    }

    public Optional<Type> getSuperType() {
        return Optional.ofNullable(typeClass.getSuperclass())
                .map(aClass -> new JavaClassType(aClass.getName()));
    }

    public List<Field> getFields() {
        return Collections.emptyList();
    }

    public List<FunctionSignature> getFunctionSignatures() {
        return Collections.emptyList();
    }

    @Override
    public int inheritsFrom(Type type) {
        if (type.getName().equals(this.getName())) {
            return 0;
        }
        return -1;
    }

    @Override
    public Optional<Type> nearestDenominator(Type type) {
        if (type.getName().equals(this.getName())) {
            return Optional.of(type);
        }
        return Optional.empty();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getTypeClass() {
        return typeClass;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getInternalName() {
        return getDescriptor();
    }

    @Override
    public int getStoreVariableOpcode() {
        return opcodes.getStore();
    }

    @Override
    public int getReturnOpcode() {
        return opcodes.getReturn();
    }

    @Override
    public int getDupCode() {
        return opcodes.getDupCode();
    }

    @Override
    public int getDupX1Code() {
        return opcodes.getDupX1Code();
    }

    @Override
    public boolean isPrimitive() {
        return opcodes.isPrimitive();
    }

    @Override
    public Nullability isNullable() {
        return Nullability.NOT_NULL;
    }

    @Override
    public org.objectweb.asm.Type getAsmType() {
        return org.objectweb.asm.Type.getType(getDescriptor());
    }

    @Override
    public String toString() {
        return "BuiltInType{" +
                "name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }


}
