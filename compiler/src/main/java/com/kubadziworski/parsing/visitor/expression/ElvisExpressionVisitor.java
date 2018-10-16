package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.ElvisExpression;
import com.kubadziworski.domain.node.expression.Expression;

public class ElvisExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public ElvisExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }
    public Expression visitElvisExpression(EnkelParser.ElvisExpressionContext ctx) {
        return new ElvisExpression(new RuleContextElementImpl(ctx), ctx.expression(0).accept(expressionVisitor), ctx.expression(1).accept(expressionVisitor));
    }
}
