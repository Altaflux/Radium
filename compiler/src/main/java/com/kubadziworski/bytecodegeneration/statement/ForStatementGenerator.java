package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.node.expression.ConditionalExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.statement.RangedForStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.LocalVariable;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ForStatementGenerator {
    private final MethodVisitor methodVisitor;

    public ForStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(RangedForStatement rangedForStatement, StatementGenerator generatord) {

        FunctionScope newScope = rangedForStatement.getScope();
        StatementGenerator nGenerator = new StatementGeneratorFilter(null, generatord, newScope);

        Statement iterator = rangedForStatement.getIteratorVariableStatement();
        Label incrementationSection = new Label();
        Label decrementationSection = new Label();
        Label endLoopSection = new Label();
        String iteratorVarName = rangedForStatement.getIteratorVarName();
        Expression endExpression = rangedForStatement.getEndExpression();
        LocalVariable variable = new LocalVariable(iteratorVarName, rangedForStatement.getType());
        Expression iteratorVariable = new LocalVariableReference(variable);
        ConditionalExpression iteratorGreaterThanEndConditional = new ConditionalExpression(iteratorVariable, endExpression, CompareSign.GREATER);
        ConditionalExpression iteratorLessThanEndConditional = new ConditionalExpression(iteratorVariable, endExpression, CompareSign.LESS);

        iterator.accept(nGenerator);

        iteratorLessThanEndConditional.accept(nGenerator);
        methodVisitor.visitJumpInsn(Opcodes.IFNE, incrementationSection);

        iteratorGreaterThanEndConditional.accept(nGenerator);
        methodVisitor.visitJumpInsn(Opcodes.IFNE, decrementationSection);

        methodVisitor.visitLabel(incrementationSection);
        rangedForStatement.getStatement().accept(nGenerator);
        methodVisitor.visitIincInsn(newScope.getLocalVariableIndex(iteratorVarName), 1);
        iteratorGreaterThanEndConditional.accept(nGenerator);
        methodVisitor.visitJumpInsn(Opcodes.IFEQ, incrementationSection);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLoopSection);

        methodVisitor.visitLabel(decrementationSection);
        rangedForStatement.getStatement().accept(nGenerator);
        methodVisitor.visitIincInsn(newScope.getLocalVariableIndex(iteratorVarName), -1);
        iteratorLessThanEndConditional.accept(nGenerator);
        methodVisitor.visitJumpInsn(Opcodes.IFEQ, decrementationSection);

        methodVisitor.visitLabel(endLoopSection);

    }
}