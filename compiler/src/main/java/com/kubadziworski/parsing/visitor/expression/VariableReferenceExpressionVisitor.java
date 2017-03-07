package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelParser.VarReferenceContext;
import com.kubadziworski.antlr.EnkelParser.VariableReferenceContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.AccessException;
import com.kubadziworski.exception.ClassNotFoundForNameException;
import com.kubadziworski.exception.FieldNotFoundException;
import com.kubadziworski.util.PropertyAccessorsUtil;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Optional;

public class VariableReferenceExpressionVisitor extends EnkelParserBaseVisitor<Expression> {
    private final Scope scope;
    private final ExpressionVisitor expressionVisitor;

    public VariableReferenceExpressionVisitor(Scope scope, ExpressionVisitor expressionVisitor) {
        this.scope = scope;
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitVarReference(VarReferenceContext ctx) {
        String varName = ctx.variableReference().getText();
        boolean ownerIsExplicit = ctx.owner != null;
        if (ownerIsExplicit) {
            try {
                Expression owner = ctx.owner.accept(expressionVisitor);
                return visitReference(ctx, varName, owner);
            } catch (ClassNotFoundForNameException | FieldNotFoundException e) {
                String possibleClass = ctx.owner.getText();
                return visitStaticReference(possibleClass, ctx);
            }
        }
        return visitReference(ctx, varName, null);
    }

    @Override
    public Expression visitVariableReference(VariableReferenceContext ctx) {
        String varName = ctx.getText();
        return visitReference(ctx, varName, null);
    }

    private Reference visitStaticReference(String possibleClass, VarReferenceContext ctx) {
        Type classType = scope.resolveClassName(possibleClass);
        Field field = classType.getField(ctx.variableReference().getText());
        return new FieldReference(new RuleContextElementImpl(ctx), field, new EmptyExpression(field.getOwner()));
    }

    private Expression visitReference(ParserRuleContext ctx, String varName, Expression owner) {

        if (owner != null) {
            Field field = owner.getType().getField(varName);
            if (field.getModifiers().contains(Modifier.STATIC)) {
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
            if (variable.isVisible()) {
                return new LocalVariableReference(new RuleContextElementImpl(ctx), variable);
            }
        }

        Field field = scope.getField(varName);
        validateAccessToField(field);

        if (field.getModifiers().contains(Modifier.STATIC)) {
            return new FieldReference(new RuleContextElementImpl(ctx), field, new EmptyExpression(field.getOwner()));
        }

        Type thisType = scope.getClassType();
        LocalVariable thisVariable = new LocalVariable("this", thisType);
        LocalVariableReference thisReference = new LocalVariableReference(new RuleContextElementImpl(ctx), thisVariable);
        return generateFieldReference(ctx, field, thisReference);
    }

    private void validateAccessToField(Field field) {
        Type classType = scope.getClassType();
        Optional<FunctionSignature> signatureOpt = PropertyAccessorsUtil.getGetterFunctionSignatureForField(field);
        boolean accessThruFunction = signatureOpt
                .map(functionSignature -> !PropertyAccessorsUtil.isFunctionAccessible(functionSignature, classType))
                .orElse(false);
        if (!accessThruFunction && !PropertyAccessorsUtil.isFunctionAccessible(field, classType)) {
            throw new AccessException("Cannot set field: " + field);
        }
    }

    private Expression generateFieldReference(ParserRuleContext ctx, Field field, Expression owner) {
        return new FieldReference(new RuleContextElementImpl(ctx), field, owner);
    }
}