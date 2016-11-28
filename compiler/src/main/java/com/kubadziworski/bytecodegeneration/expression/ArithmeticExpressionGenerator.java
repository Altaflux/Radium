package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.node.expression.arthimetic.PureArithmeticExpression;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

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
        throw new RuntimeException("Addition must only be done to String types");
    }


    public void generate(PureArithmeticExpression pureArithmeticExpression, StatementGenerator statementGenerator) {
        InstructionAdapter ad = new InstructionAdapter(methodVisitor);
        Expression leftExpression = pureArithmeticExpression.getLeftExpression();
        leftExpression.accept(statementGenerator);
        PrimitiveTypesWrapperFactory.coerce(pureArithmeticExpression.getType(), leftExpression.getType(), ad);

        Expression rightExpression = pureArithmeticExpression.getRightExpression();
        rightExpression.accept(statementGenerator);
        PrimitiveTypesWrapperFactory.coerce(pureArithmeticExpression.getType(), rightExpression.getType(), ad);
        methodVisitor.visitInsn(pureArithmeticExpression.getOperator().getOperationOpCode(pureArithmeticExpression.getType()));
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
