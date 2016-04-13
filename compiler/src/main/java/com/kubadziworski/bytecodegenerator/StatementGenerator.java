package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.domain.expression.FunctionCall;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.statement.*;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

/**
 * Created by kuba on 29.03.16.
 */
public class StatementGenerator {

    private MethodVisitor methodVisitor;
    private ExpressionGenrator expressionGenrator;
    private Scope scope;

    public StatementGenerator(MethodVisitor methodVisitor, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
        this.expressionGenrator = new ExpressionGenrator(methodVisitor,scope);
    }

    public void generate(PrintStatement printStatement) {
        Expression expression = printStatement.getExpression();
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        expression.accept(expressionGenrator);
        Type type = expression.getType();
        String descriptor = "(" + type.getDescriptor() + ")V";
        ClassType owner = new ClassType("java.io.PrintStream");
        String fieldDescriptor = owner.getDescriptor();
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, fieldDescriptor, "println", descriptor, false);
    }

    public void generate(VariableDeclarationStatement variableDeclarationStatement) {
        Expression expression = variableDeclarationStatement.getExpression();
        String name = variableDeclarationStatement.getName();
        int index = scope.getLocalVariableIndex(name);
        Type type = expression.getType();
        expression.accept(expressionGenrator);
        if (type == BultInType.INT || type == BultInType.BOOLEAN) {
            methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
        }
    }

    public void generate(FunctionCall functionCall) {
        functionCall.accept(expressionGenrator);
    }

    public void generate(ReturnStatement returnStatement) {
        Expression expression = returnStatement.getExpression();
        Type type = expression.getType();
        expression.accept(expressionGenrator);
        if(type == BultInType.VOID) {
            methodVisitor.visitInsn(Opcodes.RETURN);
        } else if (type == BultInType.INT) {
            methodVisitor.visitInsn(Opcodes.IRETURN);
        }
    }

    public void generate(IfStatement ifStatement) {
        Expression condition = ifStatement.getCondition();
        condition.accept(expressionGenrator);

        Label trueLabel = new Label();
        methodVisitor.visitJumpInsn(Opcodes.IFEQ,trueLabel);
        ifStatement.getTrueStatement().accept(this);
        Label falseLabel = new Label();
        methodVisitor.visitJumpInsn(Opcodes.GOTO,falseLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitFrame(Opcodes.F_SAME,0,null,0,null);
        ifStatement.getFalseStatement().accept(this);
        methodVisitor.visitLabel(falseLabel);
        methodVisitor.visitFrame(Opcodes.F_SAME,0,null,0,null);
    }

    public void generate(Block block) {
        Scope newScope = block.getScope();
        List<Statement> statements = block.getStatements();
        StatementGenerator statementGenerator = new StatementGenerator(methodVisitor, newScope);
        statements.stream().forEach(stmt -> stmt.accept(statementGenerator));
    }
}
