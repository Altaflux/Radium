package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.PopExpression;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class PopExpressionGenerator {

    private final MethodVisitor methodVisitor;
    private final ExpressionGenerator expressionGenerator;

    public PopExpressionGenerator(MethodVisitor methodVisitor, ExpressionGenerator expressionGenerator) {
        this.methodVisitor = methodVisitor;
        this.expressionGenerator = expressionGenerator;
    }

    public void generate(PopExpression popExpression) {

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
