package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.expression.ConditionalExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.expression.IfStatementExpressionVisitor;
import com.kubadziworski.parsing.visitor.expression.TryCatchExpressionVisitor;


/**
 * Created by kuba on 01.04.16.
 */
public class StatementVisitor extends EnkelParserBaseVisitor<Statement> {

    private final ExpressionVisitor expressionVisitor;
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
    public Statement visitThrowStatement(EnkelParser.ThrowStatementContext ctx) {
        return throwStatementVisitor.visitThrowStatement(ctx);
    }

    @Override
    public Statement visitTryExpression(EnkelParser.TryExpressionContext ctx) {
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
    public Statement visitFunctionContent(EnkelParser.FunctionContentContext ctx) {
        return functionContentVisitor.visitFunctionContent(ctx);
    }

    @Override
    public Statement visitVariableDeclaration(VariableDeclarationContext ctx) {
        return variableDeclarationStatementVisitor.visitVariableDeclaration(ctx);
    }

    @Override
    public Statement visitReturnVoid(ReturnVoidContext ctx) {
        return returnStatementVisitor.visitReturnVoid(ctx);
    }

    @Override
    public Statement visitReturnWithValue(ReturnWithValueContext ctx) {
        return returnStatementVisitor.visitReturnWithValue(ctx);
    }

    @Override
    public Statement visitBlock(BlockContext ctx) {
        return blockStatementVisitor.visitBlock(ctx);
    }

    @Override
    public Statement visitIfExpression(EnkelParser.IfExpressionContext ctx) {
        return ifStatementVisitor.visitIfExpression(ctx);
    }

    @Override
    public Expression visitVarReference(VarReferenceContext ctx) {
        return expressionVisitor.visitVarReference(ctx);
    }

    @Override
    public Expression visitPrefixExpression(EnkelParser.PrefixExpressionContext ctx) {
        return expressionVisitor.visitPrefixExpression(ctx);
    }

    @Override
    public Expression visitSuffixExpression(EnkelParser.SuffixExpressionContext ctx) {
        return expressionVisitor.visitSuffixExpression(ctx);
    }

    public Expression visitThisReference(EnkelParser.ThisReferenceContext ctx) {
        return expressionVisitor.visitThisReference(ctx);
    }

    @Override
    public Expression visitVariableReference(EnkelParser.VariableReferenceContext ctx) {
        return expressionVisitor.visitVariableReference(ctx);
    }

    @Override
    public Expression visitValue(ValueContext ctx) {
        return expressionVisitor.visitValue(ctx);
    }

    @Override
    public Expression visitFunctionCall(FunctionCallContext ctx) {
        return expressionVisitor.visitFunctionCall(ctx);
    }

    @Override
    public Expression visitConstructorCall(ConstructorCallContext ctx) {
        return expressionVisitor.visitConstructorCall(ctx);
    }

    @Override
    public Expression visitBinaryExpression(BinaryExpressionContext ctx) {
        return expressionVisitor.visitBinaryExpression(ctx);
    }

    @Override
    public ConditionalExpression visitConditionalExpression(ConditionalExpressionContext ctx) {
        return expressionVisitor.visitConditionalExpression(ctx);
    }

    @Override
    public Statement visitForStatement(EnkelParser.ForStatementContext ctx) {
        return forStatementVisitor.visitForStatement(ctx);
    }

    @Override
    public Statement visitAssignment(EnkelParser.AssignmentContext ctx) {
        return assignmentStatementVisitor.visitAssignment(ctx);
    }

    @Override
    public Expression visitParenthesisExpression(EnkelParser.ParenthesisExpressionContext ctx) {
        return expressionVisitor.visitParenthesisExpression(ctx);
    }
}
