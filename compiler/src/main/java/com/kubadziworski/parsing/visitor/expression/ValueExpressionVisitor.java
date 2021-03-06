package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser.ValueContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.util.ValueCtxResolver;

public class ValueExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ExpressionVisitor expressionVisitor;

    public ValueExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitValue(ValueContext ctx) {
        if (ctx.stringLiteral() != null) {
            return ValueCtxResolver.handleStringValue(ctx, expressionVisitor);
        }
        return ValueCtxResolver.getValueFromContext(ctx);
    }
}