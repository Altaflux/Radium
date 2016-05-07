package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ExpressionContext;
import com.kubadziworski.domain.expression.ConditionalExpression;
import com.kubadziworski.domain.expression.EmptyExpression;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
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
        if(ctx.falseStatement != null) {
            Statement falseStatement = ctx.falseStatement.accept(this);
            return new IfStatement(condition, trueStatement, falseStatement);
        }
        return new IfStatement(condition, trueStatement);
    }

    @Override
    public Expression visitVarReference(@NotNull EnkelParser.VarReferenceContext ctx) {
        return expressionVisitor.visitVarReference(ctx);
    }

    @Override
    public Expression visitValue(@NotNull EnkelParser.ValueContext ctx) {
        return expressionVisitor.visitValue(ctx);
    }

    @Override
    public Expression visitFunctionCall(@NotNull EnkelParser.FunctionCallContext ctx) {
        return expressionVisitor.visitFunctionCall(ctx);
    }

    @Override
    public Expression visitConstructorCall(@NotNull EnkelParser.ConstructorCallContext ctx) {
        return expressionVisitor.visitConstructorCall(ctx);
    }

    @Override
    public Expression visitSupercall(@NotNull EnkelParser.SupercallContext ctx) {
        return expressionVisitor.visitSupercall(ctx);
    }

    @Override
    public Expression visitADD(@NotNull EnkelParser.ADDContext ctx) {
        return expressionVisitor.visitADD(ctx);
    }

    @Override
    public Expression visitMULTIPLY(@NotNull EnkelParser.MULTIPLYContext ctx) {
        return expressionVisitor.visitMULTIPLY(ctx);
    }

    @Override
    public Expression visitSUBSTRACT(@NotNull EnkelParser.SUBSTRACTContext ctx) {
        return expressionVisitor.visitSUBSTRACT(ctx);
    }

    @Override
    public Expression visitDIVIDE(@NotNull EnkelParser.DIVIDEContext ctx) {
        return expressionVisitor.visitDIVIDE(ctx);
    }

    @Override
    public ConditionalExpression visitConditionalExpression(@NotNull EnkelParser.ConditionalExpressionContext ctx) {
        return expressionVisitor.visitConditionalExpression(ctx);
    }

}
