package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.IfExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by plozano on 10/30/2016.
 */
public class IfExpressionGenerator {

    private final ExpressionGenerator expressionGenerator;
    private final MethodVisitor methodVisitor;

    public IfExpressionGenerator(ExpressionGenerator expressionGenerator, MethodVisitor methodVisitor) {
        this.expressionGenerator = expressionGenerator;
        this.methodVisitor = methodVisitor;
    }

    public void generate(IfExpression ifStatement) {
        Expression condition = ifStatement.getCondition();
        condition.accept(expressionGenerator);
        Label trueLabel = new Label();
        Label endLabel = new Label();
        methodVisitor.visitJumpInsn(Opcodes.IFNE, trueLabel);
        Expression falseStatement = ifStatement.getFalseStatement();
        falseStatement.accept(expressionGenerator);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        ifStatement.getTrueStatement().accept(expressionGenerator);
        methodVisitor.visitLabel(endLabel);
    }
}
