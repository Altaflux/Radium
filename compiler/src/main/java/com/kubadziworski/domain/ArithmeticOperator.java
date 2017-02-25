package com.kubadziworski.domain;

import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;


public enum ArithmeticOperator {

    ADD("+", "plus", Opcodes.IADD),
    SUBTRACT("-", "minus", Opcodes.ISUB),
    DIVIDE("/", "div", Opcodes.IDIV),
    MOD("%", "mod", Opcodes.IREM),
    MULTIPLY("*", "times", Opcodes.IMUL),
    BINAND("&", "and", Opcodes.IAND),
    BINOR("|", "or", Opcodes.IOR);

    private final String operator;
    private final String methodName;
    private final int operationOpCode;

    ArithmeticOperator(String operator, String methodName, int opCode) {
        this.operator = operator;
        this.operationOpCode = opCode;
        this.methodName = methodName;
    }

    public int getOperationOpCode(Type type) {
        return type.getAsmType().getOpcode(operationOpCode);
    }

    public String getMethodName() {
        return methodName;
    }

    public static ArithmeticOperator fromString(String sign) {
        return Arrays.stream(values()).filter(opSign -> opSign.operator.equals(sign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sign not implemented"));
    }

    public static ArithmeticOperator fromMethodName(String methodName) {
        return Arrays.stream(values()).filter(opSign -> opSign.methodName.equals(methodName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Method not implemented"));
    }

}
