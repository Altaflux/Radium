package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.domain.expression.*;
import com.kubadziworski.domain.global.CompareSign;
import com.kubadziworski.domain.scope.Scope;
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
        Type type = expression.getType();
        expression.accept(expressionGenrator);
        AssignmentStatement assignmentStatement = new AssignmentStatement(variableDeclarationStatement);
        generate(assignmentStatement);
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
        Label endLabel = new Label();
        methodVisitor.visitJumpInsn(Opcodes.IFNE,trueLabel);
        ifStatement.getFalseStatement().accept(this);
        methodVisitor.visitJumpInsn(Opcodes.GOTO,endLabel);
        methodVisitor.visitLabel(trueLabel);
        ifStatement.getTrueStatement().accept(this);
        methodVisitor.visitLabel(endLabel);
    }

    public void generate(Block block) {
        Scope newScope = block.getScope();
        List<Statement> statements = block.getStatements();
        StatementGenerator statementGenerator = new StatementGenerator(methodVisitor, newScope);
        statements.stream().forEach(stmt -> stmt.accept(statementGenerator));
    }

    public void generate(RangedForStatement rangedForStatement) {
        Scope newScope = rangedForStatement.getScope();
        StatementGenerator scopeGeneratorWithNewScope = new StatementGenerator(methodVisitor, newScope);
        ExpressionGenrator exprGeneratorWithNewScope = new ExpressionGenrator(methodVisitor, newScope);
        Statement iterator = rangedForStatement.getIteratorVariableStatement();
        Label incrementationSection = new Label();
        Label decrementationSection = new Label();
        Label endLoopSection = new Label();
        String iteratorVarName = rangedForStatement.getIteratorVarName();
        Expression endExpression = rangedForStatement.getEndExpression();
        Expression iteratorVariable = new VarReference(iteratorVarName, rangedForStatement.getType());
        ConditionalExpression iteratorGreaterThanEndConditional = new ConditionalExpression(iteratorVariable, endExpression, CompareSign.GREATER);
        ConditionalExpression iteratorLessThanEndConditional = new ConditionalExpression(iteratorVariable, endExpression, CompareSign.LESS);

        iterator.accept(scopeGeneratorWithNewScope);

        iteratorLessThanEndConditional.accept(exprGeneratorWithNewScope);
        methodVisitor.visitJumpInsn(Opcodes.IFNE,incrementationSection);

        iteratorGreaterThanEndConditional.accept(exprGeneratorWithNewScope);
        methodVisitor.visitJumpInsn(Opcodes.IFNE,decrementationSection);

        methodVisitor.visitLabel(incrementationSection);
        rangedForStatement.getStatement().accept(scopeGeneratorWithNewScope);
        methodVisitor.visitIincInsn(newScope.getLocalVariableIndex(iteratorVarName),1);
        iteratorGreaterThanEndConditional.accept(exprGeneratorWithNewScope);
        methodVisitor.visitJumpInsn(Opcodes.IFEQ,incrementationSection);
        methodVisitor.visitJumpInsn(Opcodes.GOTO,endLoopSection);

        methodVisitor.visitLabel(decrementationSection);
        rangedForStatement.getStatement().accept(scopeGeneratorWithNewScope);
        methodVisitor.visitIincInsn(newScope.getLocalVariableIndex(iteratorVarName),-1);
        iteratorLessThanEndConditional.accept(exprGeneratorWithNewScope);
        methodVisitor.visitJumpInsn(Opcodes.IFEQ,decrementationSection);

        methodVisitor.visitLabel(endLoopSection);
    }

    public void generate(AssignmentStatement assignmentStatement) {
        String varName = assignmentStatement.getVarName();
        Type type = assignmentStatement.getExpression().getType();
        int index = scope.getLocalVariableIndex(varName);
        if (type == BultInType.INT || type == BultInType.BOOLEAN) {
            methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
        }
    }
}
