package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.expression.FunctionCall;
import com.kubadziworski.antlr.domain.expression.Value;
import com.kubadziworski.antlr.domain.expression.FunctionParameter;
import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.antlr.domain.scope.FunctionSignature;
import com.kubadziworski.exception.BadArgumentsSize;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
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
    public Expression visitIdentifier(@NotNull EnkelParser.IdentifierContext ctx) {
        return scope.getIdentifier(ctx.getText());
    }

    @Override
    public Expression visitValue(@NotNull EnkelParser.ValueContext ctx) {
        String value = ctx.getText();
        TypeVisitor typeVisitor = new TypeVisitor();
        Type type = ctx.accept(typeVisitor);
        return new Value(type, value);
    }

    @Override
    public Expression visitFunctionCall(@NotNull EnkelParser.FunctionCallContext ctx) {

        String funName = ctx.functionName().getText();
        FunctionSignature functionSignature = scope.getSignatureForName(funName);
        List<FunctionParameter> functionPArameters = functionSignature.getArguments();
        List<EnkelParser.ExpressionContext> calledParameters = ctx.expressionList().expression();
        if(functionPArameters.size() != calledParameters.size()) {
            throw new BadArgumentsSize(functionSignature,calledParameters);
        }
        for(int i = 0; i< functionPArameters.size(); i++) {
            List<Expression> parameters = new ArrayList<>();
            FunctionParameter formalParam = functionPArameters.get(i);
            EnkelParser.ExpressionContext actualParam = calledParameters.get(i);
            //TODO check arguments types
            ExpressionVisitor visitor = new ExpressionVisitor(scope);
            Expression parameter = actualParam.accept(visitor);
        }
        List<Expression> arguments = calledParameters.stream()
                .map((expressionContext) -> {
                    int paramIndex = calledParameters.indexOf(expressionContext);
                    Type paramType = functionPArameters.get(paramIndex).getType();
                    return expressionContext.accept(new ExpressionVisitor(scope));
                })
                .collect(Collectors.toList());
        Type returnType = functionSignature.getReturnType();
        return new FunctionCall(functionSignature, arguments,null);
    }
}
