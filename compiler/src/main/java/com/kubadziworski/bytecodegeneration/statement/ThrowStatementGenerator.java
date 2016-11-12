package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.statement.ThrowStatement;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ThrowStatementGenerator {

    private final MethodVisitor methodVisitor;

    public ThrowStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ThrowStatement throwStatement, StatementGenerator statementGenerator){
        throwStatement.getExpression().accept(statementGenerator);
        methodVisitor.visitInsn(Opcodes.ATHROW);
    }
}
