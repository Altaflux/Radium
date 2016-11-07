package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.arthimetic.*;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ArithmeticExpressionGenerator {

    private final MethodVisitor methodVisitor;

    public ArithmeticExpressionGenerator(MethodVisitor methodVisitor) {

        this.methodVisitor = methodVisitor;
    }

    public void generate(Addition expression, StatementGenerator statementGenerator) {
        if (expression.getType().equals(DefaultTypes.STRING)) {
            generateStringAppend(expression, statementGenerator);
            return;
        }
        evaluateArithmeticComponents(expression, statementGenerator);
        Type type = expression.getType();
        methodVisitor.visitInsn(type.getAddOpcode());
    }

    public void generate(Subtraction expression, StatementGenerator statementGenerator) {
        Type type = expression.getType();
        evaluateArithmeticComponents(expression, statementGenerator);
        methodVisitor.visitInsn(type.getSubstractOpcode());
    }

    public void generate(Multiplication expression, StatementGenerator statementGenerator) {
        evaluateArithmeticComponents(expression, statementGenerator);
        Type type = expression.getType();
        methodVisitor.visitInsn(type.getMultiplyOpcode());
    }

    public void generate(Division expression, StatementGenerator statementGenerator) {
        evaluateArithmeticComponents(expression, statementGenerator);
        Type type = expression.getType();
        methodVisitor.visitInsn(type.getDividOpcode());
    }

    private void evaluateArithmeticComponents(ArthimeticExpression expression, StatementGenerator statementGenerator) {
        Expression leftExpression = expression.getLeftExpression();
        Expression rightExpression = expression.getRightExpression();
        leftExpression.accept(statementGenerator);
        rightExpression.accept(statementGenerator);
    }

    private void generateStringAppend(Addition expression, StatementGenerator statementGenerator) {
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        Expression leftExpression = expression.getLeftExpression();
        leftExpression.accept(statementGenerator);
        String leftExprDescriptor = leftExpression.getType().getDescriptor();
        String descriptor = "(" + leftExprDescriptor + ")Ljava/lang/StringBuilder;";
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        Expression rightExpression = expression.getRightExpression();
        rightExpression.accept(statementGenerator);
        String rightExprDescriptor = rightExpression.getType().getDescriptor();
        descriptor = "(" + rightExprDescriptor + ")Ljava/lang/StringBuilder;";
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    }
}