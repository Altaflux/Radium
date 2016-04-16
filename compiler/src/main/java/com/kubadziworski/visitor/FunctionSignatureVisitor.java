package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.FunctionParameterContext;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.expression.FunctionParameter;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignatureVisitor extends EnkelBaseVisitor<FunctionSignature> {

    private final Scope scope;
    private final ExpressionVisitor expressionVisitor;

    public FunctionSignatureVisitor(Scope scope) {
        this.scope = scope;
        this.expressionVisitor = new ExpressionVisitor(scope);
    }

    @Override
    public FunctionSignature visitFunctionDeclaration(@NotNull EnkelParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.functionName().getText();
        List<FunctionParameterContext> argsCtx = ctx.functionParameter();
        List<FunctionParameter> parameters = new ArrayList<>();
        for(int i=0;i<argsCtx.size();i++) {
            FunctionParameterContext argCtx = argsCtx.get(i);
            String name = argCtx.ID().getText();
            Type type = TypeResolver.getFromTypeName(argCtx.type());
            Optional<Expression> defaultValue = getParameterDefaultValue(argCtx);
            FunctionParameter functionParameters = new FunctionParameter(name, type, defaultValue);
            parameters.add(functionParameters);
        }
        Type returnType = TypeResolver.getFromTypeName(ctx.type());
        return new FunctionSignature(functionName, parameters, returnType);
    }

    private Optional<Expression> getParameterDefaultValue(FunctionParameterContext argCtx) {
        if(argCtx.functionParamdefaultValue() != null) {
            EnkelParser.ExpressionContext defaultValueCtx = argCtx.functionParamdefaultValue().expression();
            return Optional.of(defaultValueCtx.accept(expressionVisitor));
        }
        return Optional.empty();
    }
}
