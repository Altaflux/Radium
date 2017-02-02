package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Expression;
import org.antlr.v4.runtime.misc.NotNull;


public class ParenthesisExpressionVisitor extends EnkelBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public ParenthesisExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    public Expression visitParenthesisExpression(@NotNull EnkelParser.ParenthesisExpressionContext ctx) {
        return ctx.expression().accept(expressionVisitor);
    }
}
