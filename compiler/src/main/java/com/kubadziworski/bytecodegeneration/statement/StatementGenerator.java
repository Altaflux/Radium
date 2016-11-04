package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.node.expression.arthimetic.Division;
import com.kubadziworski.domain.node.expression.arthimetic.Multiplication;
import com.kubadziworski.domain.node.expression.arthimetic.Substraction;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.node.statement.*;

public interface StatementGenerator {

    void generate(TryCatchStatement tryCatchStatement);

    void generate(BlockExpression blockExpression);

    void generate(IfExpression ifExpression);

    void generate(UnaryExpression unaryExpression);

    void generate(PrintStatement printStatement);

    void generate(VariableDeclaration variableDeclaration);

    void generate(DupExpression dupExpression);

    void generate(IncrementDecrementExpression incrementDecrementExpression);

    void generate(FunctionCall functionCall);

    void generate(ReturnStatement returnStatement);

    void generate(IfStatement ifStatement);

    void generate(Block block);

    void generate(RangedForStatement rangedForStatement);

    void generate(Assignment assignment);

    void generate(SuperCall superCall);

    void generate(ConstructorCall constructorCall);

    void generate(Addition addition);

    void generate(Parameter parameter);

    void generate(ConditionalExpression conditionalExpression);

    void generate(Multiplication multiplication);

    void generate(Value value);

    void generate(Substraction substraction);

    void generate(Division division);

    void generate(EmptyExpression emptyExpression);

    void generate(LocalVariableReference localVariableReference);

    void generate(FieldReference fieldReference);

    void generate(PopExpression popExpression);
}
