package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.node.expression.arthimetic.Division;
import com.kubadziworski.domain.node.expression.arthimetic.Multiplication;
import com.kubadziworski.domain.node.expression.arthimetic.Substraction;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.node.statement.*;


public class StatementGeneratorFilter implements StatementGenerator {

    protected StatementGenerator next;

    public StatementGeneratorFilter(StatementGenerator next) {
        this.next = next;
    }

    @Override
    public void generate(TryCatchStatement tryCatchStatement) {
        next.generate(tryCatchStatement);
    }

    @Override
    public void generate(BlockExpression blockExpression) {
        next.generate(blockExpression);
    }

    @Override
    public void generate(IfExpression ifExpression) {
        next.generate(ifExpression);
    }

    @Override
    public void generate(UnaryExpression unaryExpression) {
        next.generate(unaryExpression);
    }

    @Override
    public void generate(PrintStatement printStatement) {
        next.generate(printStatement);
    }

    @Override
    public void generate(VariableDeclaration variableDeclaration) {
        next.generate(variableDeclaration);
    }

    @Override
    public void generate(DupExpression dupExpression) {
        next.generate(dupExpression);
    }

    @Override
    public void generate(IncrementDecrementExpression incrementDecrementExpression) {
        next.generate(incrementDecrementExpression);
    }

    @Override
    public void generate(FunctionCall functionCall) {
        next.generate(functionCall);
    }

    @Override
    public void generate(ReturnStatement returnStatement) {
        next.generate(returnStatement);
    }

    @Override
    public void generate(IfStatement ifStatement) {
        next.generate(ifStatement);
    }

    @Override
    public void generate(Block block) {
        next.generate(block);
    }

    @Override
    public void generate(RangedForStatement rangedForStatement) {
        next.generate(rangedForStatement);

    }

    @Override
    public void generate(Assignment assignment) {
        next.generate(assignment);
    }

    @Override
    public void generate(SuperCall superCall) {
        next.generate(superCall);
    }

    @Override
    public void generate(ConstructorCall constructorCall) {
        next.generate(constructorCall);
    }

    @Override
    public void generate(Addition addition) {
        next.generate(addition);
    }

    @Override
    public void generate(Parameter parameter) {
        next.generate(parameter);
    }

    @Override
    public void generate(ConditionalExpression conditionalExpression) {
        next.generate(conditionalExpression);
    }

    @Override
    public void generate(Multiplication multiplication) {
        next.generate(multiplication);
    }

    @Override
    public void generate(Value value) {
        next.generate(value);
    }

    @Override
    public void generate(Substraction substraction) {
        next.generate(substraction);
    }

    @Override
    public void generate(Division division) {
        next.generate(division);
    }

    @Override
    public void generate(EmptyExpression emptyExpression) {
        next.generate(emptyExpression);
    }

    @Override
    public void generate(LocalVariableReference localVariableReference) {
        next.generate(localVariableReference);
    }

    @Override
    public void generate(FieldReference fieldReference) {
        next.generate(fieldReference);
    }

    @Override
    public void generate(PopExpression popExpression) {
        next.generate(popExpression);
    }
}
