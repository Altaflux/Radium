package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.AddContext;
import com.kubadziworski.antlr.EnkelParser.BlockContext;
import com.kubadziworski.antlr.EnkelParser.ConditionalExpressionContext;
import com.kubadziworski.antlr.EnkelParser.ConstructorCallContext;
import com.kubadziworski.antlr.EnkelParser.DivideContext;
import com.kubadziworski.antlr.EnkelParser.FunctionCallContext;
import com.kubadziworski.antlr.EnkelParser.IfStatementContext;
import com.kubadziworski.antlr.EnkelParser.MultiplyContext;
import com.kubadziworski.antlr.EnkelParser.PrintStatementContext;
import com.kubadziworski.antlr.EnkelParser.ReturnVoidContext;
import com.kubadziworski.antlr.EnkelParser.ReturnWithValueContext;
import com.kubadziworski.antlr.EnkelParser.SubstractContext;
import com.kubadziworski.antlr.EnkelParser.SupercallContext;
import com.kubadziworski.antlr.EnkelParser.ValueContext;
import com.kubadziworski.antlr.EnkelParser.VarReferenceContext;
import com.kubadziworski.antlr.EnkelParser.VariableDeclarationContext;
import com.kubadziworski.domain.node.expression.ConditionalExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.*;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
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
    private final IfStatementVisitor ifStatementVisitor;
    private final ForStatementVisitor forStatementVisitor;
    private final AssignmentStatementVisitor assignmentStatementVisitor;
    private final FunctionContentVisitor functionContentVisitor;
    private final TryCatchStatementVisitor tryCatchStatementVisitor;

    public StatementVisitor(Scope scope) {
        expressionVisitor = new ExpressionVisitor(scope);
        printStatementVisitor = new PrintStatementVisitor(expressionVisitor);
        variableDeclarationStatementVisitor = new VariableDeclarationStatementVisitor(expressionVisitor, scope);
        returnStatementVisitor = new ReturnStatementVisitor(expressionVisitor);
        blockStatementVisitor = new BlockStatementVisitor(scope);
        ifStatementVisitor = new IfStatementVisitor(this, expressionVisitor);
        forStatementVisitor = new ForStatementVisitor(scope);
        assignmentStatementVisitor = new AssignmentStatementVisitor(expressionVisitor, scope);
        functionContentVisitor = new FunctionContentVisitor(scope);
        tryCatchStatementVisitor = new TryCatchStatementVisitor(this, scope);
    }

    @Override
    public Statement visitTryStatement(@NotNull EnkelParser.TryStatementContext ctx) {
        return tryCatchStatementVisitor.visitTryStatement(ctx);
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
    public Block visitFunctionContent(@NotNull EnkelParser.FunctionContentContext ctx) {
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
    public Statement visitIfStatement(@NotNull IfStatementContext ctx) {
        return ifStatementVisitor.visitIfStatement(ctx);
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
    public Expression visitAdd(@NotNull AddContext ctx) {
        return expressionVisitor.visitAdd(ctx);
    }

    @Override
    public Expression visitMultiply(@NotNull MultiplyContext ctx) {
        return expressionVisitor.visitMultiply(ctx);
    }

    @Override
    public Expression visitSubstract(@NotNull SubstractContext ctx) {
        return expressionVisitor.visitSubstract(ctx);
    }

    @Override
    public Expression visitDivide(@NotNull DivideContext ctx) {
        return expressionVisitor.visitDivide(ctx);
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
