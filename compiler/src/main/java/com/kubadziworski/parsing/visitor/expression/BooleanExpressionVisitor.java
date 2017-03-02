package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.BooleanExpression;
import com.kubadziworski.domain.node.expression.Expression;


public class BooleanExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public BooleanExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitBooleanExpression(EnkelParser.BooleanExpressionContext ctx) {
        Expression leftExpression = ctx.expression(0).accept(expressionVisitor);
        Expression rightExpression = ctx.expression(1).accept(expressionVisitor);

        return new BooleanExpression(new RuleContextElementImpl(ctx),
                leftExpression, rightExpression, ctx.cmp.getText().equals("&&"));
    }
}
