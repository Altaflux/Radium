package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.IfExpression;
import com.kubadziworski.domain.node.statement.IfStatement;
import com.kubadziworski.domain.node.statement.Statement;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class IfStatementGenerator {

    private final MethodVisitor methodVisitor;

    public IfStatementGenerator( MethodVisitor methodVisitor) {

        this.methodVisitor = methodVisitor;
    }

    public void generate(IfExpression ifStatement, StatementGenerator statementGenerator) {
        Expression condition = ifStatement.getCondition();
        condition.accept(statementGenerator);
        Label trueLabel = new Label();
        Label endLabel = new Label();
        methodVisitor.visitJumpInsn(Opcodes.IFNE, trueLabel);
        Statement falseStatement = ifStatement.getFalseStatement();
        falseStatement.accept(statementGenerator);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        ifStatement.getTrueStatement().accept(statementGenerator);
        methodVisitor.visitLabel(endLabel);
    }

    public void generate(IfStatement ifStatement, StatementGenerator statementGenerator) {
        Expression condition = ifStatement.getCondition();
        condition.accept(statementGenerator);
        Label trueLabel = new Label();
        Label endLabel = new Label();
        methodVisitor.visitJumpInsn(Opcodes.IFNE, trueLabel);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        ifStatement.getTrueStatement().accept(statementGenerator);
        methodVisitor.visitLabel(endLabel);

    }
}