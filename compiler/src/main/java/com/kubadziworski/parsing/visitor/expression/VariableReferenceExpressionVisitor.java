package com.kubadziworski.parsing.visitor.expression;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.VarReferenceContext;
import com.kubadziworski.antlr.EnkelParser.VariableReferenceContext;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PropertyAccessorsUtil;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;
import java.util.Optional;

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
                return visitReference(varName, owner);
            } catch (Throwable e) {
                String possibleClass = ctx.owner.getText();
                return visitStaticReference(possibleClass, ctx);
            }
        }
        return visitReference(varName, null);
    }

    @Override
    public Expression visitVariableReference(@NotNull VariableReferenceContext ctx) {

        String varName = ctx.getText();
        return visitReference(varName, null);
    }

    private Reference visitStaticReference(String possibleClass, VarReferenceContext ctx) {
        Type classType = scope.resolveClassName(possibleClass);
//        Type classType = ClassTypeFactory.createClassType(possibleClass);
        Field field = classType.getField(ctx.variableReference().getText());
        return new FieldReference(field, new EmptyExpression(field.getOwner()));
    }

    private Expression visitReference(@NotNull String varName, Expression owner) {

        if (owner != null) {
            Field field = owner.getType().getField(varName);
            if (Modifier.isStatic(field.getModifiers())) {
                //If the reference is static we can avoid calling the owning reference
                //and simply use the class to call it.
                //We may need to check if this doesn't causes trouble, else we use a POP after
                //calling the owner expression, for now lets not optimize...
                return new FieldReference(scope.getField(owner.getType(), varName), new PopExpression(owner));
            }
            return generateFieldReference(field, owner, varName);
        }

        if (scope.isLocalVariableExists(varName)) {
            LocalVariable variable = scope.getLocalVariable(varName);
            return new LocalVariableReference(variable);
        }

        Field field = scope.getField(varName);

        if (Modifier.isStatic(field.getModifiers())) {
            return new FieldReference(field, new EmptyExpression(field.getOwner()));
        }

        Type thisType = scope.getClassType();
        LocalVariable thisVariable = new LocalVariable("this", thisType);
        LocalVariableReference thisReference = new LocalVariableReference(thisVariable);
        return generateFieldReference(field, thisReference, varName);
    }

    private Expression generateFieldReference(Field field, Expression owner, String name) {
        //This is only to allow getter and setters field Reference
        if (!field.getName().equals(name)) {
            return new FieldReference(field, owner);
        }

        Optional<FunctionCall> functionCall = PropertyAccessorsUtil.getGetterFunctionSignatureForField(field)
                .map(functionSignature -> new PropertyAccessorCall(functionSignature, owner, field));

        if (functionCall.isPresent()) {
            return functionCall.get();
        }
        return new FieldReference(field, owner);
    }
}