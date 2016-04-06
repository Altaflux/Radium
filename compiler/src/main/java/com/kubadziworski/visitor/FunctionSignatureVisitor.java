package com.kubadziworski.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.FunctionArgumentContext;
import com.kubadziworski.antlr.domain.expression.FunctionParameter;
import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.antlr.domain.scope.FunctionSignature;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuba on 06.04.16.
 */
public class FunctionSignatureVisitor extends EnkelBaseVisitor<FunctionSignature> {
    @Override
    public FunctionSignature visitFunctionDeclaration(@NotNull EnkelParser.FunctionDeclarationContext ctx) {
        TypeVisitor typeVisitor = new TypeVisitor();
        String functionName = ctx.functionName().getText();

        List<EnkelParser.FunctionArgumentContext> argsCtx = ctx.functionArgument();
        List<FunctionParameter> parameters = new ArrayList<>();
        for(int i=0;i<argsCtx.size();i++) {
            FunctionArgumentContext argCtx = argsCtx.get(i);
            FunctionParameter functionParameters = new FunctionParameter(argCtx.ID().getText(), argCtx.type().accept(new TypeVisitor()), i);
            parameters.add(functionParameters);
        }
        Type returnType = ctx.type().accept(typeVisitor);
        return new FunctionSignature(functionName, parameters, returnType);
    }
}
