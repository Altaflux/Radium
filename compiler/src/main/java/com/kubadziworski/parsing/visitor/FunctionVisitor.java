package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.BlockContext;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.parsing.FunctionGenerator;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.statement.StatementVisitor;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 01.04.16.
 */
public class FunctionVisitor extends EnkelBaseVisitor<Function> {

    private final Scope scope;

    public FunctionVisitor(Scope scope) {
        this.scope = new Scope(scope);
    }

    @Override
    public Function visitFunction(@NotNull FunctionContext ctx) {
        FunctionSignature signature = ctx.functionDeclaration().accept(new FunctionSignatureVisitor(scope));

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
