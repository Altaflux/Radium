package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.domain.node.expression.ConditionalExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.expression.IfStatementExpressionVisitor;
import com.kubadziworski.parsing.visitor.expression.TryCatchExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;


/**
 * Created by kuba on 01.04.16.
 */
public class StatementVisitor extends EnkelBaseVisitor<Statement> {

    private final ExpressionVisitor expressionVisitor;
    private final PrintStatementVisitor printStatementVisitor;
    private final VariableDeclarationStatementVisitor variableDeclarationStatementVisitor;
    private final ReturnStatementVisitor returnStatementVisitor;
    private final BlockStatementVisitor blockStatementVisitor;
    private final IfStatementExpressionVisitor ifStatementVisitor;
    private final ForStatementVisitor forStatementVisitor;
    private final AssignmentStatementVisitor assignmentStatementVisitor;
    private final FunctionContentVisitor functionContentVisitor;
    private final ThrowStatementVisitor throwStatementVisitor;
    private final TryCatchExpressionVisitor tryCatchExpressionVisitor;

    public StatementVisitor(Scope scope) {
        expressionVisitor = new ExpressionVisitor(scope);
        printStatementVisitor = new PrintStatementVisitor(expressionVisitor);
        variableDeclarationStatementVisitor = new VariableDeclarationStatementVisitor(expressionVisitor, scope);
        returnStatementVisitor = new ReturnStatementVisitor(expressionVisitor);
        blockStatementVisitor = new BlockStatementVisitor(scope);
        ifStatementVisitor = new IfStatementExpressionVisitor(expressionVisitor);
        forStatementVisitor = new ForStatementVisitor(scope);
        assignmentStatementVisitor = new AssignmentStatementVisitor(expressionVisitor, scope);
        functionContentVisitor = new FunctionContentVisitor(scope);
        throwStatementVisitor = new ThrowStatementVisitor(expressionVisitor);
        tryCatchExpressionVisitor = new TryCatchExpressionVisitor(expressionVisitor, scope);
    }

    @Override
    public Statement visitNotNullCastExpression(EnkelParser.NotNullCastExpressionContext ctx) {
        return expressionVisitor.visitNotNullCastExpression(ctx);
    }

    @Override
    public Statement visitThrowStatement(@NotNull EnkelParser.ThrowStatementContext ctx) {
        return throwStatementVisitor.visitThrowStatement(ctx);
    }

    @Override
    public Statement visitTryExpression(@NotNull EnkelParser.TryExpressionContext ctx) {
        return tryCatchExpressionVisitor.visitTryExpression(ctx);
    }

    @Override
    public Expression visitSignExpression(EnkelParser.SignExpressionContext ctx) {
        return expressionVisitor.visitSignExpression(ctx);
    }

    @Override
    public Expression visitUnaryExpression(EnkelParser.UnaryExpressionContext ctx) {
        return expressionVisitor.visitUnaryExpression(ctx);
    }

    @Override
    public Statement visitFunctionContent(@NotNull EnkelParser.FunctionContentContext ctx) {
        return functionContentVisitor.visitFunctionContent(ctx);
    }

    @Override
    public Statement visitPrintStatement(@NotNull PrintStatementContext ctx) {
        return printStatementVisitor.visitPrintStatement(ctx);
    }

    @Override
    public Statement visitVariableDeclaration(@NotNull VariableDeclarationContext ctx) {
        return variableDeclarationStatementVisitor.visitVariableDeclaration(ctx);
    }

    @Override
    public Statement visitReturnVoid(@NotNull ReturnVoidContext ctx) {
        return returnStatementVisitor.visitReturnVoid(ctx);
    }

    @Override
    public Statement visitReturnWithValue(@NotNull ReturnWithValueContext ctx) {
        return returnStatementVisitor.visitReturnWithValue(ctx);
    }

    @Override
    public Statement visitBlock(@NotNull BlockContext ctx) {
        return blockStatementVisitor.visitBlock(ctx);
    }

    @Override
    public Statement visitIfExpression(@NotNull EnkelParser.IfExpressionContext ctx) {
        return ifStatementVisitor.visitIfExpression(ctx);
    }

    @Override
    public Expression visitVarReference(@NotNull VarReferenceContext ctx) {
        return expressionVisitor.visitVarReference(ctx);
    }

    @Override
    public Expression visitPrefixExpression(@NotNull EnkelParser.PrefixExpressionContext ctx) {
        return expressionVisitor.visitPrefixExpression(ctx);
    }

    @Override
    public Expression visitSuffixExpression(@NotNull EnkelParser.SuffixExpressionContext ctx) {
        return expressionVisitor.visitSuffixExpression(ctx);
    }

    public Expression visitThisReference(EnkelParser.ThisReferenceContext ctx) {
        return expressionVisitor.visitThisReference(ctx);
    }

    @Override
    public Expression visitVariableReference(@NotNull EnkelParser.VariableReferenceContext ctx) {
        return expressionVisitor.visitVariableReference(ctx);
    }

    @Override
    public Expression visitValue(@NotNull ValueContext ctx) {
        return expressionVisitor.visitValue(ctx);
    }

    @Override
    public Expression visitFunctionCall(@NotNull FunctionCallContext ctx) {
        return expressionVisitor.visitFunctionCall(ctx);
    }

    @Override
    public Expression visitConstructorCall(@NotNull ConstructorCallContext ctx) {
        return expressionVisitor.visitConstructorCall(ctx);
    }

    @Override
    public Expression visitSupercall(@NotNull SupercallContext ctx) {
        return expressionVisitor.visitSupercall(ctx);
    }

    @Override
    public Expression visitBinaryExpression(@NotNull BinaryExpressionContext ctx) {
        return expressionVisitor.visitBinaryExpression(ctx);
    }

    @Override
    public ConditionalExpression visitConditionalExpression(@NotNull ConditionalExpressionContext ctx) {
        return expressionVisitor.visitConditionalExpression(ctx);
    }

    @Override
    public Statement visitForStatement(@NotNull EnkelParser.ForStatementContext ctx) {
        return forStatementVisitor.visitForStatement(ctx);
    }

    @Override
    public Statement visitAssignment(@NotNull EnkelParser.AssignmentContext ctx) {
        return assignmentStatementVisitor.visitAssignment(ctx);
    }
}
