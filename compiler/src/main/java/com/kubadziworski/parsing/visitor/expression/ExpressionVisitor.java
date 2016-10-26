package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.AddContext;
import com.kubadziworski.antlr.EnkelParser.ConditionalExpressionContext;
import com.kubadziworski.antlr.EnkelParser.ConstructorCallContext;
import com.kubadziworski.antlr.EnkelParser.DivideContext;
import com.kubadziworski.antlr.EnkelParser.FunctionCallContext;
import com.kubadziworski.antlr.EnkelParser.MultiplyContext;
import com.kubadziworski.antlr.EnkelParser.SubstractContext;
import com.kubadziworski.antlr.EnkelParser.SupercallContext;
import com.kubadziworski.antlr.EnkelParser.ValueContext;
import com.kubadziworski.antlr.EnkelParser.PrefixExpressionContext;
import com.kubadziworski.antlr.EnkelParser.SuffixExpressionContext;
import com.kubadziworski.antlr.EnkelParser.VarReferenceContext;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.expression.function.CallExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * Created by kuba on 02.04.16.
 */
public class ExpressionVisitor extends EnkelBaseVisitor<Expression> {

    private final ArithmeticExpressionVisitor arithmeticExpressionVisitor;
    private final VariableReferenceExpressionVisitor variableReferenceExpressionVisitor;
    private final ValueExpressionVisitor valueExpressionVisitor;
    private final CallExpressionVisitor callExpressionVisitor;
    private final ConditionalExpressionVisitor conditionalExpressionVisitor;
    private final UnaryExpressionVisitor unaryExpressionVisitor;
    private final ThisExpressionVisitor thisExpressionVisitor;

    public ExpressionVisitor(Scope scope) {
        arithmeticExpressionVisitor = new ArithmeticExpressionVisitor(this);
        variableReferenceExpressionVisitor = new VariableReferenceExpressionVisitor(scope, this);
        valueExpressionVisitor = new ValueExpressionVisitor();
        callExpressionVisitor = new CallExpressionVisitor(this, scope);
        conditionalExpressionVisitor = new ConditionalExpressionVisitor(this);
        unaryExpressionVisitor = new UnaryExpressionVisitor(this);
        thisExpressionVisitor = new ThisExpressionVisitor(scope);
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
    public Expression visitPrefixExpression(@NotNull PrefixExpressionContext ctx) {
        return unaryExpressionVisitor.visitPrefixExpression(ctx);
    }

    @Override
    public Expression visitSuffixExpression(@NotNull SuffixExpressionContext ctx) {
        return unaryExpressionVisitor.visitSuffixExpression(ctx);
    }


    @Override
    public Expression visitVarReference(@NotNull VarReferenceContext ctx) {
        return variableReferenceExpressionVisitor.visitVarReference(ctx);
    }

    @Override
    public Expression visitVariableReference(@NotNull EnkelParser.VariableReferenceContext ctx) {
        return variableReferenceExpressionVisitor.visitVariableReference(ctx);
    }

    @Override
    public Expression visitValue(@NotNull ValueContext ctx) {
        return valueExpressionVisitor.visitValue(ctx);
    }

    @Override
    public Expression visitFunctionCall(@NotNull FunctionCallContext ctx) {

        return callExpressionVisitor.visitFunctionCall(ctx);
    }

    @Override
    public Expression visitConstructorCall(@NotNull ConstructorCallContext ctx) {
        return callExpressionVisitor.visitConstructorCall(ctx);
    }

    @Override
    public Expression visitSupercall(@NotNull SupercallContext ctx) {
        return callExpressionVisitor.visitSupercall(ctx);
    }

    @Override
    public Expression visitAdd(@NotNull AddContext ctx) {

        return arithmeticExpressionVisitor.visitAdd(ctx);
    }

    @Override
    public Expression visitMultiply(@NotNull MultiplyContext ctx) {

        return arithmeticExpressionVisitor.visitMultiply(ctx);
    }

    @Override
    public Expression visitSubstract(@NotNull SubstractContext ctx) {

        return arithmeticExpressionVisitor.visitSubstract(ctx);
    }

    @Override
    public Expression visitDivide(@NotNull DivideContext ctx) {

        return arithmeticExpressionVisitor.visitDivide(ctx);
    }

    @Override
    public ConditionalExpression visitConditionalExpression(@NotNull ConditionalExpressionContext ctx) {
        return conditionalExpressionVisitor.visitConditionalExpression(ctx);
    }
}
