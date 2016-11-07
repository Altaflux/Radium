package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.UnarySign;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.exception.CompilationException;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class UnaryExpressionGenerator {

    private final MethodVisitor methodVisitor;

    public UnaryExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(UnaryExpression unaryExpression, StatementGenerator expressionGenerator) {
        Expression expression = unaryExpression.getExpression();

        if (unaryExpression.getUnarySign().equals(UnarySign.NEGATION)) {
            doBooleanNegation(expression, expressionGenerator);
        } else if (unaryExpression.getUnarySign().equals(UnarySign.SUB)) {
            doSubtraction(expression, expressionGenerator);
        }
    }

    public void doBooleanNegation(Expression expression, StatementGenerator expressionGenerator) {
        if (!expression.getType().equals(BuiltInType.BOOLEAN)) {
            throw new CompilationException();
        }

        Label endLabel = new Label();
        Label trueLabel = new Label();
        expression.accept(expressionGenerator);
        methodVisitor.visitJumpInsn(Opcodes.IFNE, trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_1);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_0);
        methodVisitor.visitLabel(endLabel);
    }

    public void doSubtraction(Expression unaryExpression, StatementGenerator expressionGenerator) {
        unaryExpression.accept(expressionGenerator);
        methodVisitor.visitInsn(unaryExpression.getType().getNegation());
    }
}
