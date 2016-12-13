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

    BOOLEAN("boolean", boolean.class, "Z", true),
    INT("int", int.class, "I", true),
    CHAR("char", char.class, "C", true),
    BYTE("byte", byte.class, "B", true),
    SHORT("short", short.class, "S", true),
    LONG("long", long.class, "J", true),
    FLOAT("float", float.class, "F", true),
    DOUBLE("double", double.class, "D", true),
    BOOLEAN_ARR("bool[]", boolean[].class, "[B", false),
    INT_ARR("int[]", int[].class, "[I", false),
    CHAR_ARR("char[]", char[].class, "[C", false),
    BYTE_ARR("byte[]", byte[].class, "[B", false),
    SHORT_ARR("short[]", short[].class, "[S", false),
    LONG_ARR("long[]", long[].class, "[J", false),
    FLOAT_ARR("float[]", float[].class, "[F", false),
    DOUBLE_ARR("double[]", double[].class, "[D", false),
    NONE("", null, "", false),
    STRING_ARR("string[]", String[].class, "[Ljava/lang/String;", false);

    private final String name;
    private final Class<?> typeClass;
    private final String descriptor;
    private final boolean primitive;

    BuiltInType(String name, Class<?> typeClass, String descriptor, boolean primitive) {
        this.name = name;
        this.typeClass = typeClass;
        this.descriptor = descriptor;
        this.primitive = primitive;

    }

    public Optional<Type> getSuperType() {
        return Optional.ofNullable(typeClass.getSuperclass())
                .map(JavaClassType::new);
    }

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
    public boolean isPrimitive() {
        return primitive;
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
