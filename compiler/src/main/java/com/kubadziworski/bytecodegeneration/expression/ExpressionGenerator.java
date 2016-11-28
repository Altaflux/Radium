package com.kubadziworski.bytecodegeneration.expression;


import com.kubadziworski.bytecodegeneration.statement.BlockStatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.IfStatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.arthimetic.*;
import com.kubadziworski.domain.node.expression.prefix.IncrementDecrementExpression;
import com.kubadziworski.domain.node.expression.prefix.UnaryExpression;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.MethodVisitor;

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
    private final IfStatementGenerator ifExpressionGenerator;
    private final ArgumentStatementGenerator argumentStatementGenerator;
    private final NotNullCastExpressionGenerator notNullCastExpressionGenerator;

    private final StatementGenerator generator;

    public ExpressionGenerator(StatementGenerator generator, MethodVisitor methodVisitor) {
        referenceExpressionGenerator = new ReferenceExpressionGenerator(methodVisitor);
        valueExpressionGenerator = new ValueExpressionGenerator(methodVisitor);
        callExpressionGenerator = new CallExpressionGenerator(methodVisitor);
        arithmeticExpressionGenerator = new ArithmeticExpressionGenerator(methodVisitor);
        conditionalExpressionGenerator = new ConditionalExpressionGenerator(methodVisitor);
        parameterExpressionGenerator = new ParameterExpressionGenerator(methodVisitor);
        prefixExpressionGenerator = new PrefixExpressionGenerator(methodVisitor);
        popExpressionGenerator = new PopExpressionGenerator(methodVisitor);
        dupExpressionGenerator = new DupExpressionGenerator(methodVisitor);
        unaryExpressionGenerator = new UnaryExpressionGenerator(methodVisitor);
        blockStatementGenerator = new BlockStatementGenerator(methodVisitor);
        ifExpressionGenerator = new IfStatementGenerator(methodVisitor);
        argumentStatementGenerator = new ArgumentStatementGenerator(methodVisitor);
        notNullCastExpressionGenerator = new NotNullCastExpressionGenerator(methodVisitor);
        this.generator = generator;
    }

    public void generate(NotNullCastExpression castExpression, StatementGenerator statementGenerator) {
        notNullCastExpressionGenerator.generate(castExpression, statementGenerator);
    }

    public void generate(Argument argument, StatementGenerator statementGenerator) {
        argumentStatementGenerator.generate(argument, statementGenerator);
    }

    public void generate(IfExpression ifExpression, StatementGenerator statementGenerator) {
        ifExpressionGenerator.generate(ifExpression, statementGenerator);
    }

    public void generate(BlockExpression unaryExpression, StatementGenerator generator) {
        blockStatementGenerator.generate(unaryExpression, false, generator);
    }

    public void generate(UnaryExpression unaryExpression, StatementGenerator generator) {
        unaryExpressionGenerator.generate(unaryExpression, generator);
    }

    public void generate(DupExpression dupExpression, StatementGenerator statementGenerator) {
        dupExpressionGenerator.generate(dupExpression, statementGenerator);
    }

    public void generate(FieldReference reference, StatementGenerator generator) {
        referenceExpressionGenerator.generate(reference, generator);
    }

    public void generateDup(FieldReference reference, StatementGenerator generator) {
        referenceExpressionGenerator.generateDup(reference, generator);
    }

    public void generate(LocalVariableReference reference) {
        referenceExpressionGenerator.generate(reference, getScope());
    }

    public void generate(IncrementDecrementExpression incrementDecrementExpression, StatementGenerator statementGenerator) {
        prefixExpressionGenerator.generate(incrementDecrementExpression, getScope(), statementGenerator);
    }

    public void generate(Parameter parameter) {
        parameterExpressionGenerator.generate(parameter, getScope());
    }

    public void generate(Value value) {
        valueExpressionGenerator.generate(value);
    }

    public void generate(ConstructorCall constructorCall, StatementGenerator statementGenerator) {
        callExpressionGenerator.generate(constructorCall, getScope(), statementGenerator);
    }

    public void generate(SuperCall superCall, StatementGenerator statementGenerator) {
        callExpressionGenerator.generate(superCall, getScope(), statementGenerator);
    }

    public void generate(FunctionCall functionCall, StatementGenerator statementGenerator) {
        callExpressionGenerator.generate(functionCall, statementGenerator);
    }

    public void generate(PureArithmeticExpression expression, StatementGenerator statementGenerator) {
        arithmeticExpressionGenerator.generate(expression, statementGenerator);
    }

    public void generate(Addition expression, StatementGenerator statementGenerator) {
        arithmeticExpressionGenerator.generate(expression, statementGenerator);
    }


    public void generate(ConditionalExpression conditionalExpression, StatementGenerator statementGenerator) {
        conditionalExpressionGenerator.generate(conditionalExpression, statementGenerator);
    }

    public void generate(PopExpression popExpression, StatementGenerator generator) {
        popExpressionGenerator.generate(popExpression, generator);
    }

    public void generate(EmptyExpression emptyExpression) {
        //do nothing ;)
    }

    public Scope getScope() {
        return generator.getScope();
    }
}
