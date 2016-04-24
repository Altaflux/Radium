package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ExpressionContext;
import com.kubadziworski.domain.expression.EmptyExpression;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.statement.*;
import com.kubadziworski.domain.type.BultInType;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by kuba on 01.04.16.
 */
public class StatementVisitor extends EnkelBaseVisitor<Statement> {

    private final Scope scope;
    private final ExpressionVisitor expressionVisitor;

    public StatementVisitor(Scope scope) {
        this.scope = scope;
        expressionVisitor = new ExpressionVisitor(scope);
    }

    @Override
    public Statement visitPrintStatement(@NotNull EnkelParser.PrintStatementContext ctx) {
        EnkelParser.ExpressionContext expressionCtx = ctx.expression();
        Expression expression = expressionCtx.accept(expressionVisitor);
        return new PrintStatement(expression);
    }

    @Override
    public Statement visitVariableDeclaration(@NotNull EnkelParser.VariableDeclarationContext ctx) {
        String varName = ctx.name().getText();
        ExpressionContext expressionCtx = ctx.expression();
        Expression expression = expressionCtx.accept(expressionVisitor);
        scope.addLocalVariable(new LocalVariable(varName, expression.getType()));
        return new VariableDeclarationStatement(varName, expression);
    }

    @Override
    public Statement visitFunctionCall(@NotNull EnkelParser.FunctionCallContext ctx) {
        return (Statement) ctx.accept(new ExpressionVisitor(scope));
    }

    @Override
    public Statement visitRETURNVOID(@NotNull EnkelParser.RETURNVOIDContext ctx) {
        return new ReturnStatement(new EmptyExpression(BultInType.VOID));
    }

    @Override
    public Statement visitRETURNWITHVALUE(@NotNull EnkelParser.RETURNWITHVALUEContext ctx) {
        Expression expression = ctx.expression().accept(expressionVisitor);
        return new ReturnStatement(expression);
    }

    @Override
    public Statement visitBlock(@NotNull EnkelParser.BlockContext ctx) {
        List<EnkelParser.StatementContext> blockstatementsCtx = ctx.statement();
        Scope newScope = new Scope(scope);
        StatementVisitor statementVisitor = new StatementVisitor(newScope);
        List<Statement> statements = blockstatementsCtx.stream().map(smtt -> smtt.accept(statementVisitor)).collect(toList());
        return new Block(newScope, statements);
    }

    @Override
    public Statement visitIfStatement(@NotNull EnkelParser.IfStatementContext ctx) {
        ExpressionContext conditionalExpressionContext = ctx.expression();
        Expression condition = conditionalExpressionContext.accept(expressionVisitor);
        Statement trueStatement = ctx.trueStatement.accept(this);
        Statement falseStatement = ctx.falseStatement.accept(this);

        return new IfStatement(condition, trueStatement, falseStatement);
    }

    @Override
    public Statement visitForStatement(@NotNull EnkelParser.ForStatementContext ctx) {
        ForStatementVisitor forStatementVisitor = new ForStatementVisitor(scope);
        return ctx.accept(forStatementVisitor);
    }


}
