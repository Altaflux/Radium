package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.BlockStatementGenerator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.*;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by kuba on 02.04.16.
 */
public class ExpressionGenerator {


    private final ReferenceExpressionGenerator referenceExpressionGenerator;
    private final ValueExpressionGenerator valueExpressionGenerator;
    private final CallExpressionGenerator callExpressionGenerator;
    private final ArithmeticExpressionGenerator arithmeticExpressionGenerator;
    private final ConditionalExpressionGenerator conditionalExpressionGenerator;
    private final ParameterExpressionGenerator parameterExpressionGenerator;
    private final PrefixExpressionGenerator prefixExpressionGenerator;
    private final PopExpressionGenerator popExpressionGenerator;
    private final DupExpressionGenerator dupExpressionGenerator;
    private final UnaryExpressionGenerator unaryExpressionGenerator;
    private final BlockStatementGenerator blockStatementGenerator;
    private final IfExpressionGenerator ifExpressionGenerator;

    public ExpressionGenerator(MethodVisitor methodVisitor, Scope scope) {
        referenceExpressionGenerator = new ReferenceExpressionGenerator(methodVisitor, scope, this);
        valueExpressionGenerator = new ValueExpressionGenerator(methodVisitor);
        callExpressionGenerator = new CallExpressionGenerator(this, scope, methodVisitor);
        arithmeticExpressionGenerator = new ArithmeticExpressionGenerator(this, methodVisitor);
        conditionalExpressionGenerator = new ConditionalExpressionGenerator(this, methodVisitor);
        parameterExpressionGenerator = new ParameterExpressionGenerator(methodVisitor, scope);
        prefixExpressionGenerator = new PrefixExpressionGenerator(methodVisitor, this, scope);
        popExpressionGenerator = new PopExpressionGenerator(methodVisitor, this);
        dupExpressionGenerator = new DupExpressionGenerator(methodVisitor, this);
        unaryExpressionGenerator = new UnaryExpressionGenerator(methodVisitor, this);
        blockStatementGenerator = new BlockStatementGenerator(methodVisitor);
        ifExpressionGenerator = new IfExpressionGenerator(this, methodVisitor);
    }

    public void generate(IfExpression ifExpression) {
        ifExpressionGenerator.generate(ifExpression);
    }

    public void generate(BlockExpression unaryExpression) {
        blockStatementGenerator.generate(unaryExpression, false);
    }

    public void generate(UnaryExpression unaryExpression) {
        unaryExpressionGenerator.generate(unaryExpression);
    }

    public void generate(DupExpression dupExpression) {
        dupExpressionGenerator.generate(dupExpression);
    }

    public void generate(FieldReference reference) {
        referenceExpressionGenerator.generate(reference);
    }

    public void generateDup(FieldReference reference) {
        referenceExpressionGenerator.generateDup(reference);
    }

    public void generate(LocalVariableReference reference) {
        referenceExpressionGenerator.generate(reference);
    }

    public void generate(IncrementDecrementExpression incrementDecrementExpression) {
        prefixExpressionGenerator.generate(incrementDecrementExpression);
    }

    public void generate(Parameter parameter) {
        parameterExpressionGenerator.generate(parameter);
    }

    public void generate(Value value) {
        valueExpressionGenerator.generate(value);
    }

    public void generate(ConstructorCall constructorCall) {
        callExpressionGenerator.generate(constructorCall);
    }

    public void generate(SuperCall superCall) {
        callExpressionGenerator.generate(superCall);
    }

    public void generate(FunctionCall functionCall) {
        callExpressionGenerator.generate(functionCall);
    }

    public void generate(Addition expression) {
        arithmeticExpressionGenerator.generate(expression);
    }

    public void generate(Substraction expression) {
        arithmeticExpressionGenerator.generate(expression);
    }

    public void generate(Multiplication expression) {
        arithmeticExpressionGenerator.generate(expression);
    }

    public void generate(Division expression) {
        arithmeticExpressionGenerator.generate(expression);
    }

    public void generate(ConditionalExpression conditionalExpression) {
        conditionalExpressionGenerator.generate(conditionalExpression);
    }

    public void generate(PopExpression popExpression) {
        popExpressionGenerator.generate(popExpression);
    }

    public void generate(EmptyExpression emptyExpression) {
        //do nothing ;)
    }
}
