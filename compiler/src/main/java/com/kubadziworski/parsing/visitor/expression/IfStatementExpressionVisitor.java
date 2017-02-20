package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.IfExpression;
import com.kubadziworski.domain.node.statement.IfStatement;
import com.kubadziworski.domain.node.statement.Statement;


public class IfStatementExpressionVisitor extends EnkelParserBaseVisitor<Statement> {

    private final ExpressionVisitor expressionVisitor;

    public IfStatementExpressionVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Statement visitIfExpression(EnkelParser.IfExpressionContext ctx) {
        EnkelParser.ExpressionContext conditionalExpressionContext = ctx.expression();
        Expression condition = conditionalExpressionContext.accept(expressionVisitor);

        Expression trueExpression = getReturnable(ctx.trueStatement);

        if(ctx.falseStatement != null) {
            Expression falseExpression = getReturnable(ctx.falseStatement);
            return new IfExpression(new RuleContextElementImpl(ctx), condition, trueExpression, falseExpression);
        }
        return new IfStatement(new RuleContextElementImpl(ctx), condition, trueExpression);
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
