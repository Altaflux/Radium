package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ExpressionContext;
import com.kubadziworski.antlr.domain.scope.LocalVariable;
import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.statement.PrintStatement;
import com.kubadziworski.antlr.domain.statement.Statement;
import com.kubadziworski.antlr.domain.statement.VariableDeclarationStatement;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Created by kuba on 01.04.16.
 */
public class StatementVisitor extends EnkelBaseVisitor<Statement> {

    private Scope scope;

    public StatementVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Statement visitPrintStatement(@NotNull EnkelParser.PrintStatementContext ctx) {
        EnkelParser.ExpressionContext expressionCtx = ctx.expression();
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        Expression expression = expressionCtx.accept(expressionVisitor);
        return new PrintStatement(expression);
    }

    @Override
    public Statement visitVariableDeclaration(@NotNull EnkelParser.VariableDeclarationContext ctx) {
        String varName = ctx.name().getText();
        ExpressionContext expressionCtx = ctx.expression();
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        Expression expression = expressionCtx.accept(expressionVisitor);
        scope.addLocalVariable(new LocalVariable(varName,expression.getType()));
        return new VariableDeclarationStatement(varName,expression);
    }
}
