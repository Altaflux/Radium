package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.expression.Expression;


public class ParenthesisExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public ParenthesisExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    public Expression visitParenthesisExpression(EnkelParser.ParenthesisExpressionContext ctx) {
        return ctx.expression().accept(expressionVisitor);
    }
}
