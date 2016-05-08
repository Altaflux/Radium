package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.expression.*;
import com.kubadziworski.domain.expression.math.Addition;
import com.kubadziworski.domain.expression.math.Division;
import com.kubadziworski.domain.expression.math.Multiplication;
import com.kubadziworski.domain.expression.math.Substraction;
import com.kubadziworski.domain.global.CompareSign;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.FunctionNameEqualClassException;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

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
        String functionName = ctx.functionName().getText();
        if(functionName.equals(scope.getClassName())) {
            throw new FunctionNameEqualClassException(functionName);
        }
        List<EnkelParser.ArgumentContext> argumentsCtx = ctx.argument();
        List<Expression> arguments = getArgumentsForCall(argumentsCtx, functionName);
        FunctionSignature signature = scope.getMethodCallSignature(functionName,arguments);
        boolean ownerIsExplicit = ctx.owner != null;
        if(ownerIsExplicit) {
            Expression owner = ctx.owner.accept(this);
            return new FunctionCall(signature, arguments, owner);
        }
        ClassType thisType = new ClassType(scope.getClassName());
        return new FunctionCall(signature, arguments, new VarReference("this",thisType));
    }

    @Override
    public Expression visitConstructorCall(@NotNull EnkelParser.ConstructorCallContext ctx) {
        String className = ctx.className().getText();
        List<EnkelParser.ArgumentContext> argumentsCtx = ctx.argument();
        List<Expression> arguments = getArgumentsForCall(argumentsCtx, className);
        return new ConstructorCall(className, arguments);
    }

    @Override
    public Expression visitSupercall(@NotNull EnkelParser.SupercallContext ctx) {
        List<EnkelParser.ArgumentContext> argumentsCtx = ctx.argument();
        List<Expression> arguments = getArgumentsForCall(argumentsCtx, SuperCall.SUPER_IDETIFIER);
        return new SuperCall(arguments);
    }

    private List<Expression> getArgumentsForCall(List<EnkelParser.ArgumentContext> argumentsCtx,String identifier) {
        List<Expression> arguments = argumentsCtx.stream().map(a -> a.accept(this)).collect(toList());
        FunctionSignature signature = scope.getMethodCallSignature(identifier,arguments);
        Comparator<EnkelParser.ArgumentContext> argumentComparator = (arg1, arg2) -> {
            if(arg1.name() == null) return 0;
            String arg1Name = arg1.name().getText();
            String arg2Name = arg2.name().getText();
            return signature.getIndexOfParameter(arg1Name) - signature.getIndexOfParameter(arg2Name);
        };
        return argumentsCtx.stream()
                .sorted(argumentComparator)
                .map(argument -> argument.expression().accept(this))
                .collect(toList());
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
        Expression leftExpression = leftExpressionCtx.accept(this);
        Expression rightExpression = rightExpressionCtx != null ? rightExpressionCtx.accept(this) : new Value(BultInType.INT, "0");
        CompareSign cmpSign = ctx.cmp != null ? CompareSign.fromString(ctx.cmp.getText()) : CompareSign.NOT_EQUAL;
        return new ConditionalExpression(leftExpression, rightExpression, cmpSign);
    }
}
