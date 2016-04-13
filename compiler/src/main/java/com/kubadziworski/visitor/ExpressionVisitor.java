package com.kubadziworski.visitor;

import com.kubadziworski.CompareSign;
import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.expression.*;
import com.kubadziworski.domain.math.Addition;
import com.kubadziworski.domain.math.Division;
import com.kubadziworski.domain.math.Multiplication;
import com.kubadziworski.domain.math.Substraction;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kuba on 02.04.16.
 */
public class ExpressionVisitor extends EnkelBaseVisitor<Expression> {

    private Scope scope;

    public ExpressionVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Expression visitVarReference(@NotNull EnkelParser.VarReferenceContext ctx) {
        String varName = ctx.getText();
        LocalVariable localVariable = scope.getLocalVariable(varName);
        return new VarReference(varName,localVariable.getType());
    }

    @Override
    public Expression visitValue(@NotNull EnkelParser.ValueContext ctx) {
        String value = ctx.getText();
        Type type = TypeResolver.getFromValue(ctx.getText());
        return new Value(type, value);
    }

    @Override
    public Expression visitFunctionCall(@NotNull EnkelParser.FunctionCallContext ctx) {

        String funName = ctx.functionName().getText();
        FunctionSignature signature = scope.getSignature(funName);
        List<EnkelParser.ExpressionContext> calledParameters = ctx.expressionList().expression();
        List<Expression> arguments = calledParameters.stream()
                .map((expressionContext) -> expressionContext.accept(this))
                .collect(Collectors.toList());
        return new FunctionCall(signature, arguments,null);
    }

    @Override
    public Expression visitADD(@NotNull EnkelParser.ADDContext ctx) {
        EnkelParser.ExpressionContext leftExpression = ctx.expression(0);
        EnkelParser.ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(this);
        Expression rightExpress = rightExpression.accept(this);

        return new Addition(leftExpress, rightExpress);
    }

    @Override
    public Expression visitMULTIPLY(@NotNull EnkelParser.MULTIPLYContext ctx) {
        EnkelParser.ExpressionContext leftExpression = ctx.expression(0);
        EnkelParser.ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(this);
        Expression rightExpress = rightExpression.accept(this);

        return new Multiplication(leftExpress, rightExpress);
    }

    @Override
    public Expression visitSUBSTRACT(@NotNull EnkelParser.SUBSTRACTContext ctx) {
        EnkelParser.ExpressionContext leftExpression = ctx.expression(0);
        EnkelParser.ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(this);
        Expression rightExpress = rightExpression.accept(this);

        return new Substraction(leftExpress, rightExpress);
    }

    @Override
    public Expression visitDIVIDE(@NotNull EnkelParser.DIVIDEContext ctx) {
        EnkelParser.ExpressionContext leftExpression = ctx.expression(0);
        EnkelParser.ExpressionContext rightExpression = ctx.expression(1);

        Expression leftExpress = leftExpression.accept(this);
        Expression rightExpress = rightExpression.accept(this);

        return new Division(leftExpress, rightExpress);
    }

    @Override
    public ConditionalExpression visitConditionalExpression(@NotNull EnkelParser.ConditionalExpressionContext ctx) {
        EnkelParser.ExpressionContext leftExpressionCtx = ctx.expression(0);
        EnkelParser.ExpressionContext rightExpressionCtx = ctx.expression(1);
        ExpressionVisitor expressionVisitor = new ExpressionVisitor(scope);
        Expression leftExpression = leftExpressionCtx.accept(expressionVisitor);
        Expression rightExpression = rightExpressionCtx != null ? rightExpressionCtx.accept(expressionVisitor) : new Value(BultInType.INT,"0");
        CompareSign cmpSign = ctx.cmp != null ? CompareSign.fromString(ctx.cmp.getText()) : CompareSign.NOT_EQUAL;
        return new ConditionalExpression(leftExpression, rightExpression, cmpSign);
    }
}
