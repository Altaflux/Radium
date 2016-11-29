package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ParameterExpressionGenerator {

    private final MethodVisitor methodVisitor;

    public ParameterExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Parameter parameter, Scope scope) {
        Type type = parameter.getType();
        int index = scope.getLocalVariableIndex(parameter.getName());
        int opCode = type.getAsmType().getOpcode(Opcodes.ILOAD);
        methodVisitor.visitVarInsn(opCode, index);
    }
}