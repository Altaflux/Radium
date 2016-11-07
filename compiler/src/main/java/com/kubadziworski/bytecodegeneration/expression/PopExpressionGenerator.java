package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.PopExpression;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class PopExpressionGenerator  {

    private final MethodVisitor methodVisitor;


    public PopExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(PopExpression popExpression,  StatementGenerator expressionGenerator) {

        popExpression.getOwner().accept(expressionGenerator);

        switch (popExpression.getType().getStackSize()) {
            case 1: {
                methodVisitor.visitInsn(Opcodes.POP);
                break;
            }
            case 2: {
                methodVisitor.visitInsn(Opcodes.POP2);
                break;
            }
        }

    }
}
