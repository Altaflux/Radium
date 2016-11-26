package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.*;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.node.expression.trycatch.TryCatchExpression;
import com.kubadziworski.domain.node.statement.*;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;


public class BaseStatementGenerator implements StatementGenerator {

    private final PrintStatementGenerator printStatementGenerator;
    private final VariableDeclarationStatementGenerator variableDeclarationStatementGenerator;
    private final ReturnStatementGenerator returnStatementGenerator;
    private final IfStatementGenerator ifStatementGenerator;
    private final BlockStatementGenerator blockStatementGenerator;
    private final ForStatementGenerator forStatementGenerator;
    private final AssignmentStatementGenerator assignmentStatementGenerator;
    private final TryCatchStatementGenerator tryCatchStatementGenerator;
    private final ExpressionGenerator expressionGenerator;
    private final ThrowStatementGenerator throwStatementGenerator;

    private final StatementGenerator parent;
    private final MethodVisitor methodVisitor;

    private int lastLine = 0;

    public BaseStatementGenerator(StatementGenerator generator, MethodVisitor methodVisitor) {
        parent = generator;
        expressionGenerator = new ExpressionGenerator(generator, methodVisitor);
        printStatementGenerator = new PrintStatementGenerator(methodVisitor);
        variableDeclarationStatementGenerator = new VariableDeclarationStatementGenerator();
        forStatementGenerator = new ForStatementGenerator(methodVisitor);
        blockStatementGenerator = new BlockStatementGenerator(methodVisitor);
        ifStatementGenerator = new IfStatementGenerator(methodVisitor);
        returnStatementGenerator = new ReturnStatementGenerator(methodVisitor);
        assignmentStatementGenerator = new AssignmentStatementGenerator(methodVisitor);
        tryCatchStatementGenerator = new TryCatchStatementGenerator(methodVisitor);
        throwStatementGenerator = new ThrowStatementGenerator(methodVisitor);

        this.methodVisitor = methodVisitor;
    }

    private BaseStatementGenerator(StatementGenerator generator, MethodVisitor methodVisitor, int lastLine) {
        this(generator, methodVisitor);
        this.lastLine = lastLine;
    }

    @Override
    public void generate(ThrowStatement throwStatement) {
        throwStatementGenerator.generate(throwStatement, this);
    }

    @Override
    public void generate(ThrowStatement throwStatement, StatementGenerator statementGenerator) {
        generateLineNumber(throwStatement);
        throwStatementGenerator.generate(throwStatement, statementGenerator);
    }

    @Override
    public void generate(TryCatchStatement tryCatchStatement) {
        tryCatchStatementGenerator.generate(tryCatchStatement, this);
    }

    @Override
    public void generate(TryCatchStatement tryCatchStatement, StatementGenerator generator) {
        generateLineNumber(tryCatchStatement);
        tryCatchStatementGenerator.generate(tryCatchStatement, generator);
    }

    @Override
    public void generate(TryCatchExpression tryCatchExpression) {
        tryCatchStatementGenerator.generate(tryCatchExpression, this);
    }

    @Override
    public void generate(TryCatchExpression tryCatchExpression, StatementGenerator generator) {
        generateLineNumber(tryCatchExpression);
        tryCatchStatementGenerator.generate(tryCatchExpression, generator);
    }

    @Override
    public void generate(BlockExpression blockExpression) {
        expressionGenerator.generate(blockExpression, this);
    }

    @Override
    public void generate(BlockExpression blockExpression, StatementGenerator generator) {
        generateLineNumber(blockExpression);
        expressionGenerator.generate(blockExpression, generator);
    }

    public void generate(IfExpression ifExpression) {
        expressionGenerator.generate(ifExpression, this);
    }

    @Override
    public void generate(IfExpression ifExpression, StatementGenerator generator) {
        generateLineNumber(ifExpression);
        expressionGenerator.generate(ifExpression, generator);
    }

    public void generate(UnaryExpression unaryExpression) {
        expressionGenerator.generate(unaryExpression, this);
    }

    @Override
    public void generate(UnaryExpression unaryExpression, StatementGenerator generator) {
        generateLineNumber(unaryExpression);
        expressionGenerator.generate(unaryExpression, generator);
    }

    public void generate(PrintStatement printStatement) {
        printStatementGenerator.generate(printStatement, this);
    }

    @Override
    public void generate(PrintStatement printStatement, StatementGenerator generator) {
        generateLineNumber(printStatement);
        printStatementGenerator.generate(printStatement, generator);
    }

    public void generate(VariableDeclaration variableDeclaration) {
        variableDeclarationStatementGenerator.generate(variableDeclaration, this);
    }

    @Override
    public void generate(VariableDeclaration variableDeclaration, StatementGenerator generator) {
        generateLineNumber(variableDeclaration);
        variableDeclarationStatementGenerator.generate(variableDeclaration, generator);
    }

    public void generate(DupExpression dupExpression) {
        expressionGenerator.generate(dupExpression, this);
    }

    @Override
    public void generate(DupExpression dupExpression, StatementGenerator generator) {
        generateLineNumber(dupExpression);
        expressionGenerator.generate(dupExpression, generator);
    }

    public void generate(IncrementDecrementExpression incrementDecrementExpression) {
        generateLineNumber(incrementDecrementExpression);
        expressionGenerator.generate(incrementDecrementExpression, this);
    }

    @Override
    public void generate(IncrementDecrementExpression incrementDecrementExpression, StatementGenerator generator) {
        generateLineNumber(incrementDecrementExpression);
        expressionGenerator.generate(incrementDecrementExpression, generator);
    }

    public void generate(FunctionCall functionCall) {
        expressionGenerator.generate(functionCall, this);
    }

    @Override
    public void generate(FunctionCall functionCall, StatementGenerator generator) {
        generateLineNumber(functionCall);
        expressionGenerator.generate(functionCall, generator);
    }

    public void generate(ReturnStatement returnStatement) {
        returnStatementGenerator.generate(returnStatement, this);
    }

    @Override
    public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
        generateLineNumber(returnStatement);
        returnStatementGenerator.generate(returnStatement, generator);
    }

    public void generate(IfStatement ifStatement) {
        ifStatementGenerator.generate(ifStatement, this);
    }

    @Override
    public void generate(IfStatement ifStatement, StatementGenerator generator) {
        generateLineNumber(ifStatement);
        ifStatementGenerator.generate(ifStatement, generator);
    }

    public void generate(Block block) {
        blockStatementGenerator.generate(block, true, this);
    }

    @Override
    public void generate(Block block, StatementGenerator generator) {
        generateLineNumber(block);
        blockStatementGenerator.generate(block, true, generator);
    }

    public void generate(RangedForStatement rangedForStatement) {
        forStatementGenerator.generate(rangedForStatement, this);
    }

    @Override
    public void generate(RangedForStatement rangedForStatement, StatementGenerator generator) {
        generateLineNumber(rangedForStatement);
        forStatementGenerator.generate(rangedForStatement, generator);
    }

    public void generate(Assignment assignment) {
        assignmentStatementGenerator.generate(assignment, getScope(), this);
    }

    @Override
    public void generate(Assignment assignment, StatementGenerator generator) {
        generateLineNumber(assignment);
        assignmentStatementGenerator.generate(assignment, getScope(), generator);
    }

    public void generate(SuperCall superCall) {
        expressionGenerator.generate(superCall, this);
    }

    @Override
    public void generate(SuperCall superCall, StatementGenerator generator) {
        generateLineNumber(superCall);
        expressionGenerator.generate(superCall, generator);
    }

    public void generate(ConstructorCall constructorCall) {
        expressionGenerator.generate(constructorCall, this);
    }

    @Override
    public void generate(ConstructorCall constructorCall, StatementGenerator generator) {
        generateLineNumber(constructorCall);
        expressionGenerator.generate(constructorCall, generator);
    }

    @Override
    public void generate(PureArithmeticExpression addition) {
        expressionGenerator.generate(addition, this);
    }

    @Override
    public void generate(PureArithmeticExpression addition, StatementGenerator generator) {
        generateLineNumber(addition);
        expressionGenerator.generate(addition, generator);
    }

    public void generate(Addition addition) {
        expressionGenerator.generate(addition, this);
    }

    @Override
    public void generate(Addition addition, StatementGenerator generator) {
        generateLineNumber(addition);
        expressionGenerator.generate(addition, generator);
    }

    public void generate(Parameter parameter) {
        expressionGenerator.generate(parameter);
    }

    public void generate(Argument argument, StatementGenerator generator) {
        expressionGenerator.generate(argument, generator);
    }

    public void generate(Argument argument) {
        expressionGenerator.generate(argument, this);
    }

    @Override
    public void generate(Parameter parameter, StatementGenerator generator) {
        generateLineNumber(parameter);
        expressionGenerator.generate(parameter);
    }

    public void generate(ConditionalExpression conditionalExpression) {
        expressionGenerator.generate(conditionalExpression, this);
    }

    @Override
    public void generate(ConditionalExpression conditionalExpression, StatementGenerator generator) {
        generateLineNumber(conditionalExpression);
        expressionGenerator.generate(conditionalExpression, generator);
    }

    public void generate(Multiplication multiplication) {
        expressionGenerator.generate(multiplication, this);
    }

    @Override
    public void generate(Multiplication multiplication, StatementGenerator generator) {
        generateLineNumber(multiplication);
        expressionGenerator.generate(multiplication, generator);
    }

    public void generate(Value value) {
        expressionGenerator.generate(value);
    }

    @Override
    public void generate(Value value, StatementGenerator generator) {
        generateLineNumber(value);
        expressionGenerator.generate(value);
    }

    public void generate(Subtraction subtraction) {
        expressionGenerator.generate(subtraction, this);
    }

    @Override
    public void generate(Subtraction subtraction, StatementGenerator generator) {
        generateLineNumber(subtraction);
        expressionGenerator.generate(subtraction, generator);
    }

    public void generate(Division division) {
        expressionGenerator.generate(division, this);
    }

    @Override
    public void generate(Division division, StatementGenerator generator) {
        generateLineNumber(division);
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
        generateLineNumber(localVariableReference);
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
        generateLineNumber(fieldReference);
        expressionGenerator.generate(fieldReference, generator);
    }

    @Override
    public void generateDup(FieldReference fieldReference, StatementGenerator generator) {
        generateLineNumber(fieldReference);
        expressionGenerator.generateDup(fieldReference, generator);
    }

    public void generate(PopExpression popExpression) {
        expressionGenerator.generate(popExpression, this);
    }

    @Override
    public void generate(PopExpression popExpression, StatementGenerator generator) {
        generateLineNumber(popExpression);
        expressionGenerator.generate(popExpression, generator);
    }

    public void generate(NotNullCastExpression castExpression) {
        expressionGenerator.generate(castExpression, this);
    }

    @Override
    public void generate(NotNullCastExpression castExpression, StatementGenerator generator) {
        generateLineNumber(castExpression);
        expressionGenerator.generate(castExpression, generator);
    }



    public Scope getScope() {
        return parent.getScope();
    }

    public StatementGenerator copy(StatementGenerator generator) {
        return new BaseStatementGenerator(generator, this.methodVisitor, this.lastLine);
    }


    private void generateLineNumber(Statement statement) {
        if (statement != null) {
            if (statement.shouldAnalyze()) {
                if (statement.getStartLine() != lastLine) {
                    Label label = new Label();
                    methodVisitor.visitLabel(label);
                    methodVisitor.visitLineNumber(statement.getStartLine(), label);
                    lastLine = statement.getStartLine();
                }
            }
        }
    }
}
