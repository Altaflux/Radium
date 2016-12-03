package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ConditionalExpressionContext;
import com.kubadziworski.antlr.EnkelParser.ExpressionContext;
import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.ConditionalExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Value;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.exception.ComparisonBetweenDiferentTypesException;
import org.antlr.v4.runtime.misc.NotNull;

public class ConditionalExpressionVisitor extends EnkelBaseVisitor<ConditionalExpression> {
    private final ExpressionVisitor expressionVisitor;

    public ConditionalExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public ConditionalExpression visitConditionalExpression(@NotNull ConditionalExpressionContext ctx) {
        ExpressionContext leftExpressionCtx = ctx.expression(0);
        ExpressionContext rightExpressionCtx = ctx.expression(1);
        Expression leftExpression = leftExpressionCtx.accept(expressionVisitor);
        Expression rightExpression = rightExpressionCtx != null ? rightExpressionCtx.accept(expressionVisitor) : new Value(PrimitiveTypes.INT_TYPE, "0");
        CompareSign cmpSign = ctx.cmp != null ? CompareSign.fromString(ctx.cmp.getText()) : CompareSign.NOT_EQUAL;

        if (leftExpression.getType().isPrimitive()) {
            if (!rightExpression.getType().equals(leftExpression.getType()) && !rightExpression.getType().equals(AnyType.INSTANCE)) {
                if (cmpSign.equals(CompareSign.EQUAL) || cmpSign.equals(CompareSign.NOT_EQUAL)) {
                    throw new ComparisonBetweenDiferentTypesException(leftExpression, rightExpression);
                }
            }
        }

        return new ConditionalExpression(new RuleContextElementImpl(ctx), leftExpression, rightExpression, cmpSign);
    }
}