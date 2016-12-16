package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.VarReferenceContext;
import com.kubadziworski.antlr.EnkelParser.VariableReferenceContext;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;

public class VariableReferenceExpressionVisitor extends EnkelBaseVisitor<Expression> {
    private final Scope scope;
    private final ExpressionVisitor expressionVisitor;

    public VariableReferenceExpressionVisitor(Scope scope, ExpressionVisitor expressionVisitor) {
        this.scope = scope;
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitVarReference(@NotNull VarReferenceContext ctx) {
        String varName = ctx.variableReference().getText();
        boolean ownerIsExplicit = ctx.owner != null;
        if (ownerIsExplicit) {
            try {
                Expression owner = ctx.owner.accept(expressionVisitor);
                return visitReference(ctx, varName, owner);
            } catch (Throwable e) {
                String possibleClass = ctx.owner.getText();
                return visitStaticReference(possibleClass, ctx);
            }
        }
        return visitReference(ctx, varName, null);
    }

    @Override
    public Expression visitVariableReference(@NotNull VariableReferenceContext ctx) {
        String varName = ctx.getText();
        return visitReference(ctx, varName, null);
    }

    private Reference visitStaticReference(String possibleClass, VarReferenceContext ctx) {
        Type classType = scope.resolveClassName(possibleClass);
        Field field = classType.getField(ctx.variableReference().getText());
        return new FieldReference(new RuleContextElementImpl(ctx), field, new EmptyExpression(field.getOwner()));
    }

    private Expression visitReference(ParserRuleContext ctx, @NotNull String varName, Expression owner) {

        if (owner != null) {
            Field field = owner.getType().getField(varName);
            if (Modifier.isStatic(field.getModifiers())) {
                //If the reference is static we can avoid calling the owning reference
                //and simply use the class to call it.
                //We may need to check if this doesn't causes trouble, else we use a POP after
                //calling the owner expression, for now lets not optimize...

                return new FieldReference(new RuleContextElementImpl(ctx), owner.getType().getField(varName), new PopExpression(owner));
            }
            return generateFieldReference(ctx, field, owner);
        }

        if (scope.isLocalVariableExists(varName)) {
            LocalVariable variable = scope.getLocalVariable(varName);
            return new LocalVariableReference(new RuleContextElementImpl(ctx), variable);
        }

        Field field = scope.getField(varName);
        if (Modifier.isStatic(field.getModifiers())) {
            return new FieldReference(new RuleContextElementImpl(ctx), field, new EmptyExpression(field.getOwner()));
        }

        Type thisType = scope.getClassType();
        LocalVariable thisVariable = new LocalVariable("this", thisType);
        LocalVariableReference thisReference = new LocalVariableReference(new RuleContextElementImpl(ctx), thisVariable);
        return generateFieldReference(ctx, field, thisReference);
    }

    private Expression generateFieldReference(ParserRuleContext ctx, Field field, Expression owner) {
        return new FieldReference(new RuleContextElementImpl(ctx), field, owner);
    }
}