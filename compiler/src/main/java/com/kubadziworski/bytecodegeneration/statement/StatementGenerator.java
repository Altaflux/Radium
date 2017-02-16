package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.*;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.node.expression.trycatch.TryCatchExpression;
import com.kubadziworski.domain.node.statement.*;
import com.kubadziworski.domain.scope.Scope;

public interface StatementGenerator {

    void generate(ThrowStatement throwStatement);
    void generate(ThrowStatement throwStatement, StatementGenerator statementGenerator);

    void generate(TryCatchStatement tryCatchStatement);

    void generate(TryCatchStatement tryCatchStatement, StatementGenerator generator);

    void generate(TryCatchExpression tryCatchExpression);

    void generate(TryCatchExpression tryCatchExpression, StatementGenerator generator);

    void generate(BlockExpression blockExpression);

    void generate(BlockExpression blockExpression, StatementGenerator generator);

    void generate(IfExpression ifExpression);

    void generate(IfExpression ifExpression, StatementGenerator generator);

    void generate(UnaryExpression unaryExpression);

    void generate(UnaryExpression unaryExpression, StatementGenerator generator);

    void generate(VariableDeclaration variableDeclaration);
    void generate(VariableDeclaration variableDeclaration, StatementGenerator generator);


    void generate(NotNullCastExpression expression);
    void generate(NotNullCastExpression expression,  StatementGenerator generator);


    void generate(DupExpression dupExpression);
    void generate(DupExpression dupExpression,  StatementGenerator generator);

    void generate(IncrementDecrementExpression incrementDecrementExpression);
    void generate(IncrementDecrementExpression incrementDecrementExpression,  StatementGenerator generator);

    void generate(FunctionCall functionCall);
    void generate(FunctionCall functionCall,  StatementGenerator generator);

    void generate(ReturnStatement returnStatement);
    void generate(ReturnStatement returnStatement,  StatementGenerator generator);

    void generate(IfStatement ifStatement);
    void generate(IfStatement ifStatement,  StatementGenerator generator);

    void generate(Block block);
    void generate(Block block,  StatementGenerator generator);

    void generate(RangedForStatement rangedForStatement);
    void generate(RangedForStatement rangedForStatement, StatementGenerator generator);

    void generate(Assignment assignment);
    void generate(Assignment assignment, StatementGenerator generator);

    void generate(FieldAssignment assignment);

    void generate(FieldAssignment assignment, StatementGenerator generator);

    void generate(SuperCall superCall);
    void generate(SuperCall superCall, StatementGenerator generator);


    void generate(ConstructorCall constructorCall);
    void generate(ConstructorCall constructorCall, StatementGenerator generator);

    void generate(Addition addition);
    void generate(Addition addition, StatementGenerator generator);

    void generate(Argument parameter);
    void generate(Argument parameter, StatementGenerator generator);

    void generate(Parameter parameter);
    void generate(Parameter parameter, StatementGenerator generator);

    void generate(ConditionalExpression conditionalExpression);
    void generate(ConditionalExpression conditionalExpression, StatementGenerator generator);


    void generate(Value value);
    void generate(Value value, StatementGenerator generator);


    void generate(EmptyExpression emptyExpression);
    void generate(EmptyExpression emptyExpression, StatementGenerator generator);

    void generate(LocalVariableReference localVariableReference);
    void generate(LocalVariableReference localVariableReference, StatementGenerator generator);

    void generate(FieldReference fieldReference);
    void generate(FieldReference fieldReference, StatementGenerator generator);

    void generate(PopExpression popExpression);
    void generate(PopExpression popExpression, StatementGenerator generator);

    Scope getScope();


    StatementGenerator copy(StatementGenerator generator);
}
