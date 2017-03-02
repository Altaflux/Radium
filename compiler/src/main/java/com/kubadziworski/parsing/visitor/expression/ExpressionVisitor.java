package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.*;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.BlockExpression;
import com.kubadziworski.domain.node.expression.ConditionalExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.NotNullCastExpression;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.expression.function.CallExpressionVisitor;
import com.kubadziworski.parsing.visitor.statement.BlockStatementVisitor;

/**
 * Created by kuba on 02.04.16.
 */
public class ExpressionVisitor extends EnkelParserBaseVisitor<Expression> {

    private final ArithmeticExpressionVisitor arithmeticExpressionVisitor;
    private final VariableReferenceExpressionVisitor variableReferenceExpressionVisitor;
    private final ValueExpressionVisitor valueExpressionVisitor;
    private final CallExpressionVisitor callExpressionVisitor;
    private final ConditionalExpressionVisitor conditionalExpressionVisitor;
    private final UnaryExpressionVisitor unaryExpressionVisitor;
    private final ThisExpressionVisitor thisExpressionVisitor;
    private final IfStatementExpressionVisitor ifStatementExpressionVisitor;
    private final BlockStatementVisitor blockStatementVisitor;
    private final TryCatchExpressionVisitor tryCatchExpressionVisitor;
    private final NotNullCastExpressionVisitor notNullCastExpressionVisitor;
    private final ParenthesisExpressionVisitor parenthesisExpressionVisitor;
    private final BooleanExpressionVisitor booleanExpressionVisitor;

    public ExpressionVisitor(Scope scope) {
        arithmeticExpressionVisitor = new ArithmeticExpressionVisitor(this);
        variableReferenceExpressionVisitor = new VariableReferenceExpressionVisitor(scope, this);
        valueExpressionVisitor = new ValueExpressionVisitor(this);
        callExpressionVisitor = new CallExpressionVisitor(this, scope);
        conditionalExpressionVisitor = new ConditionalExpressionVisitor(this);
        unaryExpressionVisitor = new UnaryExpressionVisitor(this);
        thisExpressionVisitor = new ThisExpressionVisitor(scope);
        ifStatementExpressionVisitor = new IfStatementExpressionVisitor(this);
        blockStatementVisitor = new BlockStatementVisitor(scope);
        tryCatchExpressionVisitor = new TryCatchExpressionVisitor(this, scope);
        notNullCastExpressionVisitor = new NotNullCastExpressionVisitor(this);
        parenthesisExpressionVisitor = new ParenthesisExpressionVisitor(this);
        booleanExpressionVisitor = new BooleanExpressionVisitor(this);
    }

    @Override
    public NotNullCastExpression visitNotNullCastExpression(EnkelParser.NotNullCastExpressionContext ctx) {
        return notNullCastExpressionVisitor.visitNotNullCastExpression(ctx);
    }

    @Override
    public Expression visitBlock(EnkelParser.BlockContext ctx) {
        return new BlockExpression(new RuleContextElementImpl(ctx), blockStatementVisitor.visitBlock(ctx));
    }

    @Override
    public Expression visitTryExpression(EnkelParser.TryExpressionContext ctx) {
        Statement statement = tryCatchExpressionVisitor.visitTryExpression(ctx);
        if (!(statement instanceof Expression)) {
            throw new RuntimeException("'TryCatch' not declared as an expression, at least one catch is needed");
        }
        return (Expression) statement;
    }

    @Override
    public Expression visitIfExpression(EnkelParser.IfExpressionContext ctx) {
        Statement statement = ifStatementExpressionVisitor.visitIfExpression(ctx);
        if (!(statement instanceof Expression)) {
            throw new RuntimeException("'If' not declared as an expression, both branches are needed");
        }
        return (Expression) statement;
    }

    @Override
    public Expression visitSignExpression(EnkelParser.SignExpressionContext ctx) {
        return unaryExpressionVisitor.visitSignExpression(ctx);
    }

    @Override
    public Expression visitUnaryExpression(EnkelParser.UnaryExpressionContext ctx) {
        return unaryExpressionVisitor.visitUnaryExpression(ctx);
    }

    public Expression visitThisReference(EnkelParser.ThisReferenceContext ctx) {
        return thisExpressionVisitor.visitThisReference(ctx);
    }

    @Override
    public Expression visitPrefixExpression(PrefixExpressionContext ctx) {
        return unaryExpressionVisitor.visitPrefixExpression(ctx);
    }

    @Override
    public Expression visitSuffixExpression(SuffixExpressionContext ctx) {
        return unaryExpressionVisitor.visitSuffixExpression(ctx);
    }


    @Override
    public Expression visitVarReference(VarReferenceContext ctx) {
        return variableReferenceExpressionVisitor.visitVarReference(ctx);
    }

    @Override
    public Expression visitVariableReference(EnkelParser.VariableReferenceContext ctx) {
        return variableReferenceExpressionVisitor.visitVariableReference(ctx);
    }

    @Override
    public Expression visitValue(ValueContext ctx) {
        return valueExpressionVisitor.visitValue(ctx);
    }

    @Override
    public Expression visitFunctionCall(FunctionCallContext ctx) {
        return callExpressionVisitor.visitFunctionCall(ctx);
    }

    @Override
    public Expression visitConstructorCall(ConstructorCallContext ctx) {
        return callExpressionVisitor.visitConstructorCall(ctx);
    }

    @Override
    public Expression visitSupercall(SupercallContext ctx) {
        return callExpressionVisitor.visitSupercall(ctx);
    }

    @Override
    public Expression visitBinaryExpression(BinaryExpressionContext ctx) {
        return arithmeticExpressionVisitor.visitBinaryExpression(ctx);
    }

    @Override
    public ConditionalExpression visitConditionalExpression(ConditionalExpressionContext ctx) {
        return conditionalExpressionVisitor.visitConditionalExpression(ctx);
    }

    @Override
    public Expression visitParenthesisExpression(EnkelParser.ParenthesisExpressionContext ctx) {
        return parenthesisExpressionVisitor.visitParenthesisExpression(ctx);
    }

    @Override
    public Expression visitBooleanExpression(EnkelParser.BooleanExpressionContext ctx) {
        return booleanExpressionVisitor.visitBooleanExpression(ctx);
    }
}
