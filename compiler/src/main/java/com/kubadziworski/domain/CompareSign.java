package com.kubadziworski.domain;

import org.objectweb.asm.Opcodes;

import java.util.Arrays;

/**
 * Created by kuba on 12.04.16.
 */
public enum CompareSign {
    EQUAL("==", Opcodes.IFEQ, "equals"),
    NOT_EQUAL("!=", Opcodes.IFNE, "equals"),
    LESS("<", Opcodes.IFLT, "compareTo"),
    GREATER(">", Opcodes.IFGT, "compareTo"),
    LESS_OR_EQUAL("<=", Opcodes.IFLE, "compareTo"),
    GRATER_OR_EQUAL(">=", Opcodes.IFGE, "compareTo");

    private final String sign;
    private final int opcode;
    private final String methodName;

    CompareSign(String s, int opcode, String methodName) {
        sign = s;
        this.opcode = opcode;
        this.methodName = methodName;
    }

    public int getOpcode() {
        return opcode;
    }

    public String getSign() {
        return sign;
    }

    public String getMethodName() {
        return methodName;
    }

    public static CompareSign fromString(String sign) {
        return Arrays.stream(values()).filter(cmpSign -> cmpSign.sign.equals(sign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sign not implemented"));
    }
}
