package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.node.expression.arthimetic.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.Type;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Collections;

public class ArithmeticExpressionVisitor extends EnkelBaseVisitor<Expression> {
    private final ExpressionVisitor expressionVisitor;

    public ArithmeticExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitAdd(@NotNull AddContext ctx) {
        ExpressionContext leftExpression = ctx.expression(0);
        ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(expressionVisitor);
        Expression rightExpress = rightExpression.accept(expressionVisitor);

        return createFunction(ctx, leftExpress, rightExpress, ArithmeticOperator.ADD);
    }

    @Override
    public Expression visitMultiply(@NotNull MultiplyContext ctx) {
        ExpressionContext leftExpression = ctx.expression(0);
        ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(expressionVisitor);
        Expression rightExpress = rightExpression.accept(expressionVisitor);

        return createFunction(ctx, leftExpress, rightExpress, ArithmeticOperator.MULTIPLY);
    }

    @Override
    public Expression visitSubstract(@NotNull SubstractContext ctx) {
        ExpressionContext leftExpression = ctx.expression(0);
        ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(expressionVisitor);
        Expression rightExpress = rightExpression.accept(expressionVisitor);

        return createFunction(ctx, leftExpress, rightExpress, ArithmeticOperator.SUBTRACT);
    }

    @Override
    public Expression visitDivide(@NotNull DivideContext ctx) {
        ExpressionContext leftExpression = ctx.expression(0);
        ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(expressionVisitor);
        Expression rightExpress = rightExpression.accept(expressionVisitor);

        return createFunction(ctx, leftExpress, rightExpress, ArithmeticOperator.DIVIDE);
    }

    private Expression createFunction(ExpressionContext context, Expression leftExpression, Expression rightExpression, ArithmeticOperator operator) {
        Type type = leftExpression.getType();
        Type rightType = rightExpression.getType();
        if (type.equals(DefaultTypes.STRING) || rightType.equals(DefaultTypes.STRING)) {
            return new Addition(new RuleContextElementImpl(context), leftExpression, rightExpression);
        }

        Argument argument = new Argument(rightExpression, null);
        FunctionSignature signature = type.getMethodCallSignature(operator.getMethodName(), Collections.singletonList(argument));
        if (rightExpression.getType().isPrimitive()) {
            return new PureArithmeticExpression(leftExpression, rightExpression, signature.getReturnType(), operator);
        }

        return new FunctionCall(new RuleContextElementImpl(context), signature, Collections.singletonList(argument), leftExpression);
    }
}