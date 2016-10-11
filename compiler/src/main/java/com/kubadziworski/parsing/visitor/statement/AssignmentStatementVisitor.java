package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class AssignmentStatementVisitor extends EnkelBaseVisitor<Assignment> {
    private final ExpressionVisitor expressionVisitor;

    public AssignmentStatementVisitor(ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Assignment visitAssignment(@NotNull EnkelParser.AssignmentContext ctx) {

        EnkelParser.ExpressionContext expressionCtx = ctx.postExpr;
        Expression expression = expressionCtx.accept(expressionVisitor);
        String varName = ctx.name().getText();
        if (ctx.preExp != null) {
            return new Assignment(ctx.preExp.accept(expressionVisitor), varName, expression);
        }
        return new Assignment(varName, expression);
    }
}