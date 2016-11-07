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

    private final StatementGenerator parent;
    private final MethodVisitor methodVisitor;


    public BaseStatementGenerator(StatementGenerator generator, MethodVisitor methodVisitor) {
        parent = generator;
        expressionGenerator = new ExpressionGenerator(generator, methodVisitor);
        printStatementGenerator = new PrintStatementGenerator(expressionGenerator, methodVisitor);
        variableDeclarationStatementGenerator = new VariableDeclarationStatementGenerator();
        forStatementGenerator = new ForStatementGenerator(generator, methodVisitor);
        blockStatementGenerator = new BlockStatementGenerator(methodVisitor);
        ifStatementGenerator = new IfStatementGenerator( methodVisitor);
        returnStatemenetGenerator = new ReturnStatemenetGenerator(methodVisitor);
        assignmentStatementGenerator = new AssignmentStatementGenerator( methodVisitor);
        tryCatchStatementGenerator = new TryCatchStatementGenerator(methodVisitor);
        this.methodVisitor = methodVisitor;
    }

    public void generate(TryCatchStatement tryCatchStatement) {
        tryCatchStatementGenerator.generate(tryCatchStatement, this);
    }

    @Override
    public void generate(TryCatchStatement tryCatchStatement, StatementGenerator generator) {
        tryCatchStatementGenerator.generate(tryCatchStatement, generator);
    }

    public void generate(BlockExpression blockExpression) {
        expressionGenerator.generate(blockExpression, this);
    }

    @Override
    public void generate(BlockExpression blockExpression, StatementGenerator generator) {
        expressionGenerator.generate(blockExpression, generator);
    }

    public void generate(IfExpression ifExpression) {
        expressionGenerator.generate(ifExpression, this);
    }

    @Override
    public void generate(IfExpression ifExpression, StatementGenerator generator) {
        expressionGenerator.generate(ifExpression, generator);
    }

    public void generate(UnaryExpression unaryExpression) {
        expressionGenerator.generate(unaryExpression, this);
    }

    @Override
    public void generate(UnaryExpression unaryExpression, StatementGenerator generator) {
        expressionGenerator.generate(unaryExpression, generator);
    }

    public void generate(PrintStatement printStatement) {
        printStatementGenerator.generate(printStatement, this);
    }

    @Override
    public void generate(PrintStatement printStatement, StatementGenerator generator) {
        printStatementGenerator.generate(printStatement, generator);
    }

    public void generate(VariableDeclaration variableDeclaration) {
        variableDeclarationStatementGenerator.generate(variableDeclaration, this);
    }

    @Override
    public void generate(VariableDeclaration variableDeclaration, StatementGenerator generator) {
        variableDeclarationStatementGenerator.generate(variableDeclaration, generator);
    }

    public void generate(DupExpression dupExpression) {
        expressionGenerator.generate(dupExpression, this);
    }

    @Override
    public void generate(DupExpression dupExpression, StatementGenerator generator) {
        expressionGenerator.generate(dupExpression, generator);
    }

    public void generate(IncrementDecrementExpression incrementDecrementExpression) {
        expressionGenerator.generate(incrementDecrementExpression, this);
    }

    @Override
    public void generate(IncrementDecrementExpression incrementDecrementExpression, StatementGenerator generator) {
        expressionGenerator.generate(incrementDecrementExpression, generator);
    }

    public void generate(FunctionCall functionCall) {
        expressionGenerator.generate(functionCall, this);
    }

    @Override
    public void generate(FunctionCall functionCall, StatementGenerator generator) {
        expressionGenerator.generate(functionCall, generator);
    }

    public void generate(ReturnStatement returnStatement) {
        returnStatemenetGenerator.generate(returnStatement, this);
    }

    @Override
    public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
        returnStatemenetGenerator.generate(returnStatement, generator);
    }

    public void generate(IfStatement ifStatement) {
        ifStatementGenerator.generate(ifStatement, this);
    }

    @Override
    public void generate(IfStatement ifStatement, StatementGenerator generator) {
        ifStatementGenerator.generate(ifStatement, generator);
    }

    public void generate(Block block) {
        blockStatementGenerator.generate(block, true, this);
    }

    @Override
    public void generate(Block block, StatementGenerator generator) {
        blockStatementGenerator.generate(block, true, generator);
    }

    public void generate(RangedForStatement rangedForStatement) {
        forStatementGenerator.generate(rangedForStatement, this);
    }

    @Override
    public void generate(RangedForStatement rangedForStatement, StatementGenerator generator) {
        forStatementGenerator.generate(rangedForStatement, generator);
    }

    public void generate(Assignment assignment) {
        assignmentStatementGenerator.generate(assignment, getScope(), this);
    }

    @Override
    public void generate(Assignment assignment, StatementGenerator generator) {
        assignmentStatementGenerator.generate(assignment, getScope(), generator);
    }

    public void generate(SuperCall superCall) {
        expressionGenerator.generate(superCall, this);
    }

    @Override
    public void generate(SuperCall superCall, StatementGenerator generator) {
        expressionGenerator.generate(superCall, generator);
    }

    public void generate(ConstructorCall constructorCall) {
        expressionGenerator.generate(constructorCall, this);
    }

    @Override
    public void generate(ConstructorCall constructorCall, StatementGenerator generator) {
        expressionGenerator.generate(constructorCall, generator);
    }

    public void generate(Addition addition) {
        expressionGenerator.generate(addition, this);
    }

    @Override
    public void generate(Addition addition, StatementGenerator generator) {
        expressionGenerator.generate(addition, generator);
    }

    public void generate(Parameter parameter) {
        expressionGenerator.generate(parameter);
    }

    @Override
    public void generate(Parameter parameter, StatementGenerator generator) {
        expressionGenerator.generate(parameter);
    }

    public void generate(ConditionalExpression conditionalExpression) {
        expressionGenerator.generate(conditionalExpression, this);
    }

    @Override
    public void generate(ConditionalExpression conditionalExpression, StatementGenerator generator) {
        expressionGenerator.generate(conditionalExpression, generator);
    }

    public void generate(Multiplication multiplication) {
        expressionGenerator.generate(multiplication, this);
    }

    @Override
    public void generate(Multiplication multiplication, StatementGenerator generator) {
        expressionGenerator.generate(multiplication, generator);
    }

    public void generate(Value value) {
        expressionGenerator.generate(value);
    }

    @Override
    public void generate(Value value, StatementGenerator generator) {
        expressionGenerator.generate(value);
    }

    public void generate(Substraction substraction) {
        expressionGenerator.generate(substraction, this);
    }

    @Override
    public void generate(Substraction substraction, StatementGenerator generator) {
        expressionGenerator.generate(substraction, generator);
    }

    public void generate(Division division) {
        expressionGenerator.generate(division, this);
    }

    @Override
    public void generate(Division division, StatementGenerator generator) {
        expressionGenerator.generate(division, generator);
    }

    public void generate(EmptyExpression emptyExpression) {
        expressionGenerator.generate(emptyExpression);
    }

    @Override
    public void generate(EmptyExpression emptyExpression, StatementGenerator generator) {
        expressionGenerator.generate(emptyExpression);
    }

    public void generate(LocalVariableReference localVariableReference) {
        expressionGenerator.generate(localVariableReference);
    }

    @Override
    public void generate(LocalVariableReference localVariableReference, StatementGenerator generator) {
        expressionGenerator.generate(localVariableReference);
    }

    public void generate(FieldReference fieldReference) {
        expressionGenerator.generate(fieldReference, this);
    }

    public void generateDup(FieldReference fieldReference) {
        expressionGenerator.generateDup(fieldReference, this);
    }

    @Override
    public void generate(FieldReference fieldReference, StatementGenerator generator) {
        expressionGenerator.generate(fieldReference, generator);
    }

    @Override
    public void generateDup(FieldReference fieldReference, StatementGenerator generator) {
        expressionGenerator.generateDup(fieldReference, generator);
    }

    public void generate(PopExpression popExpression) {
        expressionGenerator.generate(popExpression, this);
    }

    @Override
    public void generate(PopExpression popExpression, StatementGenerator generator) {
        expressionGenerator.generate(popExpression, generator);
    }


    @Override
    public StatementGenerator getGenerator() {
        if (parent != null) {
            return parent;
        }
        return this;
    }

    public Scope getScope() {
        return parent.getScope();
    }

    public StatementGenerator copy(StatementGenerator generator) {
        return new BaseStatementGenerator(generator, this.methodVisitor);
    }
}
