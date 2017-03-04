package com.kubadziworski.domain.node.expression.function;


import org.objectweb.asm.Opcodes;

public enum SignatureType {

    FUNCTION_CALL(Opcodes.INVOKEVIRTUAL),
    CONSTRUCTOR_CALL(Opcodes.INVOKESPECIAL);

    private final int opCode;

    SignatureType(int invokeOpcode) {
        this.opCode = invokeOpcode;
    }

    public int getOpCode() {
        return opCode;
    }
}
