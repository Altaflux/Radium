package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.ArgumentHolder;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.node.expression.arthimetic.Addition;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.DefaultTypes;
import com.kubadziworski.domain.type.Type;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Collections;

public class ArithmeticExpressionVisitor extends EnkelParserBaseVisitor<Expression> {
    private final ExpressionVisitor expressionVisitor;

    public ArithmeticExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitBinaryExpression(@NotNull BinaryExpressionContext ctx) {
        ExpressionContext leftExpression = ctx.expression(0);
        ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(expressionVisitor);
        Expression rightExpress = rightExpression.accept(expressionVisitor);
        return createFunction(ctx, leftExpress, rightExpress, ArithmeticOperator.fromString(ctx.opType.getText()));
    }

    private Expression createFunction(ExpressionContext context, Expression leftExpression, Expression rightExpression, ArithmeticOperator operator) {
        Type type = leftExpression.getType();
        Type rightType = rightExpression.getType();
        if (type.equals(DefaultTypes.STRING) || rightType.equals(DefaultTypes.STRING)) {
            return new Addition(new RuleContextElementImpl(context), leftExpression, rightExpression);
        }

        ArgumentHolder argument = new ArgumentHolder(rightExpression, null);
        FunctionSignature signature = type.getMethodCallSignature(operator.getMethodName(), Collections.singletonList(argument));
        return new FunctionCall(new RuleContextElementImpl(context), signature, signature.createArgumentList(Collections.singletonList(argument)), leftExpression);
    }
}