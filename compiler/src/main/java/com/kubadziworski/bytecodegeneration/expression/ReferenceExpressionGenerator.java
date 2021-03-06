package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.util.PropertyAccessorsGenerator;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

public class ReferenceExpressionGenerator {
    private final InstructionAdapter methodVisitor;


    public ReferenceExpressionGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(LocalVariableReference localVariableReference, FunctionScope scope) {
        String varName = localVariableReference.getName();
        int index = scope.getLocalVariableIndex(varName);
        Type type = localVariableReference.getType();
        int opCode = type.getAsmType().getOpcode(Opcodes.ILOAD);
        methodVisitor.visitVarInsn(opCode, index);
    }


    public void generate(FieldReference fieldReference, StatementGenerator expressionGenerator) {
        PropertyAccessorsGenerator.generate(fieldReference, expressionGenerator, methodVisitor);
    }
}