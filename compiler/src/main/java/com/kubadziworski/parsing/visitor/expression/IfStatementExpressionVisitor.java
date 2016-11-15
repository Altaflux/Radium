package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.IfExpression;
import com.kubadziworski.domain.node.statement.IfStatement;
import com.kubadziworski.domain.node.statement.Statement;
import org.antlr.v4.runtime.misc.NotNull;


public class IfStatementExpressionVisitor extends EnkelBaseVisitor<Statement> {

    private final ExpressionVisitor expressionVisitor;

    public IfStatementExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Statement visitIfExpression(@NotNull EnkelParser.IfExpressionContext ctx) {
        EnkelParser.ExpressionContext conditionalExpressionContext = ctx.expression();
        Expression condition = conditionalExpressionContext.accept(expressionVisitor);

        Expression trueExpression = getReturnable(ctx.trueStatement);

        if(ctx.falseStatement != null) {
            Expression falseExpression = getReturnable(ctx.falseStatement);
            return new IfExpression(condition, trueExpression, falseExpression);
        }
        return new IfStatement(condition, trueExpression);
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
