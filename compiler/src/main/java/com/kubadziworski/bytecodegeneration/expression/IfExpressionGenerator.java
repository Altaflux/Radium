package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.IfExpression;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by plozano on 10/30/2016.
 */
public class IfExpressionGenerator {

    private final MethodVisitor methodVisitor;

    public IfExpressionGenerator( MethodVisitor methodVisitor) {

        this.methodVisitor = methodVisitor;
    }

    public void generate(IfExpression ifStatement, StatementGenerator statementGenerator) {
        Expression condition = ifStatement.getCondition();
        condition.accept(statementGenerator);
        Label trueLabel = new Label();
        Label endLabel = new Label();
        methodVisitor.visitJumpInsn(Opcodes.IFNE, trueLabel);
        Expression falseStatement = ifStatement.getFalseStatement();
        falseStatement.accept(statementGenerator);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        ifStatement.getTrueStatement().accept(statementGenerator);
        methodVisitor.visitLabel(endLabel);
    }
}
