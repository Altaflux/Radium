package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;

public class AssignmentStatementVisitor extends EnkelBaseVisitor<Assignment> {
    private final ExpressionVisitor expressionVisitor;
    private final Scope scope;

    public AssignmentStatementVisitor(ExpressionVisitor expressionVisitor, Scope scope) {
        this.expressionVisitor = expressionVisitor;
        this.scope = scope;
    }

    @Override
    public Assignment visitAssignment(@NotNull EnkelParser.AssignmentContext ctx) {

        EnkelParser.ExpressionContext expressionCtx = ctx.postExpr;
        Expression expression = expressionCtx.accept(expressionVisitor);
        String varName = ctx.name().getText();
        if (ctx.preExp != null) {
            return new Assignment(ctx.preExp.accept(expressionVisitor), varName, expression);
        }
        if (scope.isLocalVariableExists(varName)) {
            return new Assignment(varName, expression);
        } else if (scope.isFieldExists(varName)) {
            return new Assignment(new LocalVariableReference(scope.getLocalVariable("this")), varName, expression);
        } else {
            throw new RuntimeException("Assignment on un-declared variable: " + varName);
        }
    }
}