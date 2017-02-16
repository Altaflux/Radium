package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.FunctionGenerator;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;

/**
 * Created by kuba on 01.04.16.
 */
public class FunctionVisitor extends EnkelParserBaseVisitor<Function> {

    private final Scope scope;

    public FunctionVisitor(Scope scope) {
        this.scope = new Scope(scope);
    }

    @Override
    public Function visitFunction(@NotNull FunctionContext ctx) {
        FunctionSignature signature = ctx.functionDeclaration().accept(new FunctionSignatureVisitor(scope));

        Scope scope = new Scope(this.scope, signature);
        if (!Modifier.isStatic(signature.getModifiers())) {
            scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        }
        FunctionGenerator generator = new FunctionGenerator(scope);
        if (signature.getName().equals(scope.getFullClassName())) {
            return generator.generateFunction(signature, ctx.functionContent(), true);
        }

        return generator.generateFunction(signature, ctx.functionContent(), false);
    }


}
