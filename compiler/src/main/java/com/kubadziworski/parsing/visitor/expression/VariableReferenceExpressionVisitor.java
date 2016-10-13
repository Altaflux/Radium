package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.VarReferenceContext;
import com.kubadziworski.antlr.EnkelParser.VariableReferenceContext;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;

import com.kubadziworski.domain.type.ClassType;
import org.antlr.v4.runtime.misc.NotNull;

public class VariableReferenceExpressionVisitor extends EnkelBaseVisitor<Reference> {
    private final Scope scope;
    private final ExpressionVisitor expressionVisitor;

    public VariableReferenceExpressionVisitor(Scope scope, ExpressionVisitor expressionVisitor) {
        this.scope = scope;
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Reference visitVarReference(@NotNull VarReferenceContext ctx) {
        String varName = ctx.variableReference().getText();
        if (ctx.owner != null) {
            try {
                return visitReference(varName, ctx.owner.accept(expressionVisitor));
            } catch (Throwable e) {
                String possibleClass = ctx.owner.getText();
                return visitStaticReference(possibleClass, ctx);
            }
        }
        return visitReference(varName, null);
    }

    @Override
    public Reference visitVariableReference(@NotNull VariableReferenceContext ctx) {

        String varName = ctx.getText();
        return visitReference(varName, null);
    }

    private Reference visitStaticReference(String possibleClass, VarReferenceContext ctx) {
        ClassType classType = new ClassType(possibleClass);
        Field field = scope.getField(classType, ctx.variableReference().getText());
        return new StaticFieldReference(field);
    }

    private Reference visitReference(@NotNull String varName, Expression owner) {

        if (owner != null) {
            return new FieldReference(scope.getField(owner.getType(), varName), owner);
        }

        if (scope.isLocalVariableExists(varName)) {
            LocalVariable variable = scope.getLocalVariable(varName);
            return new LocalVariableReference(variable);
        }

        ClassType thisType = new ClassType(scope.getClassName());
        LocalVariable thisVariable = new LocalVariable("this", thisType);
        Field field = scope.getField(varName);
        return new FieldReference(field, new LocalVariableReference(thisVariable));

    }
}