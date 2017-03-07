package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.FunctionGenerator;

/**
 * Created by kuba on 01.04.16.
 */
public class FunctionVisitor extends EnkelParserBaseVisitor<Function> {

    private final Scope scope;

    public FunctionVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Function visitFunction(FunctionContext ctx) {
        FunctionSignature signature = ctx.functionDeclaration().accept(new FunctionSignatureVisitor(scope));

        FunctionScope scope = new FunctionScope(this.scope, signature);
        if (!(signature.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC))) {
            scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        }
        FunctionGenerator generator = new FunctionGenerator(scope);
        return generator.generateFunction(signature, ctx.functionContent(), false);
    }


}
