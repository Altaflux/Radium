package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.ConditionalExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.scope.FunctionSignature;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.Collections;

public class ConditionalExpressionGenerator {

    private final InstructionAdapter methodVisitor;

    public ConditionalExpressionGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ConditionalExpression conditionalExpression, StatementGenerator statementGenerator) {
        Expression leftExpression = conditionalExpression.getLeftExpression();
        Expression rightExpression = conditionalExpression.getRightExpression();
        CompareSign compareSign = conditionalExpression.getCompareSign();

        generateObjectsComparison(leftExpression, rightExpression, compareSign, statementGenerator);
        Label endLabel = new Label();
        Label trueLabel = new Label();
        methodVisitor.visitJumpInsn(compareSign.getOpcode(), trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_0);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_1);
        methodVisitor.visitLabel(endLabel);
    }

    private void generateObjectsComparison(Expression leftExpression, Expression rightExpression, CompareSign compareSign, StatementGenerator statementGenerator) {
        switch (compareSign) {
            case EQUAL:
            case NOT_EQUAL:
                FunctionSignature equalsSignature = leftExpression.getType().getMethodCallSignature("equals", Collections.singletonList(new ArgumentHolder(rightExpression, null)));
                FunctionCall equalsCall = new FunctionCall(equalsSignature, equalsSignature.createArgumentList(Collections.singletonList(new ArgumentHolder(rightExpression, null))), leftExpression);
                equalsCall.accept(statementGenerator);
                methodVisitor.visitInsn(Opcodes.ICONST_1);
                methodVisitor.visitInsn(Opcodes.IXOR);
                break;
            case LESS:
            case GREATER:
            case LESS_OR_EQUAL:
            case GRATER_OR_EQUAL:
                FunctionSignature compareToSignature = leftExpression.getType().getMethodCallSignature("compareTo", Collections.singletonList(new ArgumentHolder(rightExpression, null)));
                FunctionCall compareToCall = new FunctionCall(compareToSignature, compareToSignature.createArgumentList(Collections.singletonList(new ArgumentHolder(rightExpression, null))), leftExpression);
                compareToCall.accept(statementGenerator);
                break;
        }
    }
}