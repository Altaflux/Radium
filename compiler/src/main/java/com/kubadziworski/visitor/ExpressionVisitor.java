package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.domain.expression.VarReference;
import com.kubadziworski.antlr.domain.expression.*;
import com.kubadziworski.antlr.domain.scope.LocalVariable;
import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.antlr.domain.scope.FunctionSignature;
import com.kubadziworski.antlr.util.TypeResolver;
import com.kubadziworski.exception.BadArgumentsSize;
import org.antlr.v4.runtime.misc.NotNull;

import javax.lang.model.type.TypeVisitor;
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
        List<FunctionParameter> signatureParameters = signature.getArguments();
        List<EnkelParser.ExpressionContext> calledParameters = ctx.expressionList().expression();
        List<Expression> arguments = calledParameters.stream()
                .map((expressionContext) -> {
                    return expressionContext.accept(new ExpressionVisitor(scope));
                })
                .collect(Collectors.toList());
        Type returnType = signature.getReturnType();
        return new FunctionCall(signature, arguments,null);
    }
}
