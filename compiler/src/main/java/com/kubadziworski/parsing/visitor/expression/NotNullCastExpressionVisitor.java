package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.NotNullCastExpression;


public class NotNullCastExpressionVisitor extends EnkelParserBaseVisitor<NotNullCastExpression> {
    private final ExpressionVisitor expressionVisitor;

    public NotNullCastExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public NotNullCastExpression visitNotNullCastExpression(EnkelParser.NotNullCastExpressionContext ctx) {
        Expression expression = ctx.expression().accept(expressionVisitor);
        return new NotNullCastExpression(new RuleContextElementImpl(ctx), expression);
    }
}
