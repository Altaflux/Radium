package com.kubadziworski.domain.type;

/**
 * Created by kuba on 02.04.16.
 */
public enum BultInType implements Type {

    BOOLEAN("boolean", boolean.class, "Z", TypeSpecificOpcodes.INT, 1),
    INT("int", int.class, "I", TypeSpecificOpcodes.INT, 1),
    CHAR("char", char.class, "C", TypeSpecificOpcodes.INT, 1),
    BYTE("byte", byte.class, "B", TypeSpecificOpcodes.INT, 1),
    SHORT("short", short.class, "S", TypeSpecificOpcodes.INT, 1),
    LONG("long", long.class, "J", TypeSpecificOpcodes.LONG, 2),
    FLOAT("float", float.class, "F", TypeSpecificOpcodes.FLOAT, 1),
    DOUBLE("double", double.class, "D", TypeSpecificOpcodes.DOUBLE, 2),
    STRING("string", String.class, "Ljava/lang/String;", TypeSpecificOpcodes.OBJECT, 1),
    BOOLEAN_ARR("bool[]", boolean[].class, "[B", TypeSpecificOpcodes.OBJECT, 1),
    INT_ARR("int[]", int[].class, "[I", TypeSpecificOpcodes.OBJECT, 1),
    CHAR_ARR("char[]", char[].class, "[C", TypeSpecificOpcodes.OBJECT, 1),
    BYTE_ARR("byte[]", byte[].class, "[B", TypeSpecificOpcodes.OBJECT, 1),
    SHORT_ARR("short[]", short[].class, "[S", TypeSpecificOpcodes.OBJECT, 1),
    LONG_ARR("long[]", long[].class, "[J", TypeSpecificOpcodes.OBJECT, 1),
    FLOAT_ARR("float[]", float[].class, "[F", TypeSpecificOpcodes.OBJECT, 1),
    DOUBLE_ARR("double[]", double[].class, "[D", TypeSpecificOpcodes.OBJECT, 1),
    STRING_ARR("string[]", String[].class, "[Ljava/lang/String;", TypeSpecificOpcodes.OBJECT, 1),
    NONE("", null, "", TypeSpecificOpcodes.OBJECT, 1),
    VOID("void", void.class, "V", TypeSpecificOpcodes.VOID, 0);

    private final String name;
    private final Class<?> typeClass;
    private final String descriptor;
    private final TypeSpecificOpcodes opcodes;
    private final int stackSize;

    BultInType(String name, Class<?> typeClass, String descriptor, TypeSpecificOpcodes opcodes, int stackSize) {
        this.name = name;
        this.typeClass = typeClass;
        this.descriptor = descriptor;
        this.opcodes = opcodes;
        this.stackSize = stackSize;
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
    public int getLoadVariableOpcode() {
        return opcodes.getLoad();
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
    public int getAddOpcode() {
        return opcodes.getAdd();
    }

    @Override
    public int getSubstractOpcode() {
        return opcodes.getSubstract();
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
    public int getMultiplyOpcode() {
        return opcodes.getMultiply();
    }

    @Override
    public int getDividOpcode() {
        return opcodes.getDivide();
    }

    @Override
    public int getStackSize() {
        return stackSize;
    }

    @Override
    public String toString() {
        return "BultInType{" +
                "name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}
