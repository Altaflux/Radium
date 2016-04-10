package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.FunctionArgumentContext;
import com.kubadziworski.domain.expression.FunctionParameter;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignatureVisitor extends EnkelBaseVisitor<FunctionSignature> {
    @Override
    public FunctionSignature visitFunctionDeclaration(@NotNull EnkelParser.FunctionDeclarationContext ctx) {
        String functionName = ctx.functionName().getText();
        List<FunctionArgumentContext> argsCtx = ctx.functionArgument();
        List<FunctionParameter> parameters = new ArrayList<>();
        for(int i=0;i<argsCtx.size();i++) {
            FunctionArgumentContext argCtx = argsCtx.get(i);
            String name = argCtx.ID().getText();
            Type type = TypeResolver.getFromTypeName(argCtx.type());
            FunctionParameter functionParameters = new FunctionParameter(name, type);
            parameters.add(functionParameters);
        }
        Type returnType = TypeResolver.getFromTypeName(ctx.type());
        return new FunctionSignature(functionName, parameters, returnType);
    }
}
