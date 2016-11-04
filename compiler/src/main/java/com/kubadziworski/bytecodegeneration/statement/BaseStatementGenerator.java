package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.node.expression.arthimetic.Division;
import com.kubadziworski.domain.node.expression.arthimetic.Multiplication;
import com.kubadziworski.domain.node.expression.arthimetic.Substraction;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.*;
import org.objectweb.asm.MethodVisitor;


public class BaseStatementGenerator implements StatementGenerator {

    private final PrintStatementGenerator printStatementGenerator;
    private final VariableDeclarationStatementGenerator variableDeclarationStatementGenerator;
    private final ReturnStatemenetGenerator returnStatemenetGenerator;
    private final IfStatementGenerator ifStatementGenerator;
    private final BlockStatementGenerator blockStatementGenerator;
    private final ForStatementGenerator forStatementGenerator;
    private final AssignmentStatementGenerator assignmentStatementGenerator;
    private final TryCatchStatementGenerator tryCatchStatementGenerator;
    private final ExpressionGenerator expressionGenerator;

    public BaseStatementGenerator(MethodVisitor methodVisitor, Scope scope) {
        expressionGenerator = new ExpressionGenerator(methodVisitor, scope);
        printStatementGenerator = new PrintStatementGenerator(expressionGenerator, methodVisitor);
        variableDeclarationStatementGenerator = new VariableDeclarationStatementGenerator(this, expressionGenerator);
        forStatementGenerator = new ForStatementGenerator(methodVisitor);
        blockStatementGenerator = new BlockStatementGenerator(methodVisitor);
        ifStatementGenerator = new IfStatementGenerator(this, expressionGenerator, methodVisitor);
        returnStatemenetGenerator = new ReturnStatemenetGenerator(expressionGenerator, methodVisitor);
        assignmentStatementGenerator = new AssignmentStatementGenerator(methodVisitor, expressionGenerator, scope);
        tryCatchStatementGenerator = new TryCatchStatementGenerator(this, methodVisitor, scope);
    }

    public void generate(TryCatchStatement tryCatchStatement) {
        tryCatchStatementGenerator.generate(tryCatchStatement);
    }

    public void generate(BlockExpression blockExpression) {
        expressionGenerator.generate(blockExpression);
    }

    public void generate(IfExpression ifExpression) {
        expressionGenerator.generate(ifExpression);
    }

    public void generate(UnaryExpression unaryExpression) {
        expressionGenerator.generate(unaryExpression);
    }

    public void generate(PrintStatement printStatement) {
        printStatementGenerator.generate(printStatement);
    }

    public void generate(VariableDeclaration variableDeclaration) {
        variableDeclarationStatementGenerator.generate(variableDeclaration);
    }

    public void generate(DupExpression dupExpression) {
        expressionGenerator.generate(dupExpression);
    }

    public void generate(IncrementDecrementExpression incrementDecrementExpression) {
        expressionGenerator.generate(incrementDecrementExpression);
    }

    public void generate(FunctionCall functionCall) {
        functionCall.accept(expressionGenerator);
    }

    public void generate(ReturnStatement returnStatement) {
        returnStatemenetGenerator.generate(returnStatement);
    }

    public void generate(IfStatement ifStatement) {
        ifStatementGenerator.generate(ifStatement);
    }

    public void generate(Block block) {
        blockStatementGenerator.generate(block, true);
    }

    public void generate(RangedForStatement rangedForStatement) {
        forStatementGenerator.generate(rangedForStatement);
    }

    public void generate(Assignment assignment) {
        assignmentStatementGenerator.generate(assignment);
    }

    public void generate(SuperCall superCall) {
        expressionGenerator.generate(superCall);
    }

    public void generate(ConstructorCall constructorCall) {
        expressionGenerator.generate(constructorCall);
    }

    public void generate(Addition addition) {
        expressionGenerator.generate(addition);
    }

    public void generate(Parameter parameter) {
        expressionGenerator.generate(parameter);
    }

    public void generate(ConditionalExpression conditionalExpression) {
        expressionGenerator.generate(conditionalExpression);
    }

    public void generate(Multiplication multiplication) {
        expressionGenerator.generate(multiplication);
    }

    public void generate(Value value) {
        expressionGenerator.generate(value);
    }

    public void generate(Substraction substraction) {
        expressionGenerator.generate(substraction);
    }

    public void generate(Division division) {
        expressionGenerator.generate(division);
    }

    public void generate(EmptyExpression emptyExpression) {
        expressionGenerator.generate(emptyExpression);
    }

    public void generate(LocalVariableReference localVariableReference) {
        expressionGenerator.generate(localVariableReference);
    }

    public void generate(FieldReference fieldReference) {
        expressionGenerator.generate(fieldReference);
    }

    public void generate(PopExpression popExpression) {
        expressionGenerator.generate(popExpression);
    }
}
