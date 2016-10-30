package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.IfExpression;
import org.antlr.v4.runtime.misc.NotNull;


public class IfStatementExpressionVisitor extends EnkelBaseVisitor<IfExpression> {

    private final ExpressionVisitor expressionVisitor;

    public IfStatementExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public IfExpression visitIfExpression(@NotNull EnkelParser.IfExpressionContext ctx) {
        EnkelParser.ExpressionContext conditionalExpressionContext = ctx.expression();
        Expression condition = conditionalExpressionContext.accept(expressionVisitor);

        Expression trueExpression = getReturnable(ctx.trueStatement);
        Expression falseExpression = getReturnable(ctx.falseStatement);
        return new IfExpression(condition, trueExpression, falseExpression);
    }

    private Expression getReturnable(EnkelParser.ReturnableContext ctx) {
        EnkelParser.BlockContext blockContext = ctx.block();
        if (blockContext != null) {
            return blockContext.accept(expressionVisitor);
        } else {
            return ctx.expression().accept(expressionVisitor);
        }
    }

}
