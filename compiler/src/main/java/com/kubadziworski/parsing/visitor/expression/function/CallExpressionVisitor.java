package com.kubadziworski.parsing.visitor.expression.function;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ArgumentListContext;
import com.kubadziworski.antlr.EnkelParser.ConstructorCallContext;
import com.kubadziworski.antlr.EnkelParser.FunctionCallContext;
import com.kubadziworski.antlr.EnkelParser.SupercallContext;

import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.FunctionNameEqualClassException;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

public class CallExpressionVisitor extends EnkelBaseVisitor<Call> {
    private final ExpressionVisitor expressionVisitor;
    private final Scope scope;
    private static final Logger LOGGER = LoggerFactory.getLogger(CallExpressionVisitor.class);

    public CallExpressionVisitor(ExpressionVisitor expressionVisitor, Scope scope) {
        this.expressionVisitor = expressionVisitor;
        this.scope = scope;
    }

    @Override
    public Call visitFunctionCall(@NotNull FunctionCallContext ctx) {
        String functionName = ctx.functionName().getText();
        if (functionName.equals(scope.getFullClassName())) {
            throw new FunctionNameEqualClassException(functionName);
        }
        List<Argument> arguments = getArgumentsForCall(ctx.argumentList());

        if(ctx.SUPER() != null){
            return createSuperFunctionCall(ctx, functionName, arguments);
        }


        boolean ownerIsExplicit = ctx.owner != null;
        if (ownerIsExplicit) {
            try {
                Expression owner = ctx.owner.accept(expressionVisitor);
                FunctionSignature signature = owner.getType().getMethodCallSignature(functionName, arguments);
                if (Modifier.isStatic(signature.getModifiers())) {
                    //If the reference is static we can avoid calling the owning reference
                    //and simply use the class to call it.
                    //We may need to check if this doesn't causes trouble, else we use a POP after
                    //calling the owner expression, for now lets not optimize...
                    return new FunctionCall(new RuleContextElementImpl(ctx), signature, arguments, new PopExpression(owner));
                }
                return new FunctionCall(new RuleContextElementImpl(ctx), signature, arguments, owner);
            } catch (Exception e) {
                String possibleClass = ctx.owner.getText();
                return visitStaticReference(possibleClass, functionName, arguments);
            }
        }

        FunctionSignature signature = scope.getMethodCallSignature(functionName, arguments);
        if (Modifier.isStatic(signature.getModifiers())) {
            return new FunctionCall(new RuleContextElementImpl(ctx), signature, arguments, signature.getOwner());
        }

        Type thisType = new EnkelType(scope.getFullClassName(), scope);
        LocalVariable thisVariable = new LocalVariable("this", thisType);
        return new FunctionCall(new RuleContextElementImpl(ctx), signature, arguments, new LocalVariableReference(thisVariable));
    }

    @Override
    public Call visitConstructorCall(@NotNull ConstructorCallContext ctx) {

        Type className = scope.resolveClassName(ctx.typeName().getText());
        List<Argument> arguments = getArgumentsForCall(ctx.argumentList());
        return new ConstructorCall(new RuleContextElementImpl(ctx), className.getName(), arguments);
    }

    @Override
    public Call visitSupercall(@NotNull SupercallContext ctx) {
        List<Argument> arguments = getArgumentsForCall(ctx.argumentList());
        return new SuperCall(new RuleContextElementImpl(ctx), arguments);
    }

    private SuperFunctionCall createSuperFunctionCall(FunctionCallContext ctx, String functionName, List<Argument> arguments) {

        FunctionSignature signature = ClassTypeFactory.createClassType(scope.getSuperClassName()).getMethodCallSignature(functionName, arguments);
        Type thisType = new EnkelType(scope.getFullClassName(), scope);
        LocalVariable thisVariable = new LocalVariable("this", thisType);

        return new SuperFunctionCall(new RuleContextElementImpl(ctx), signature, arguments, new LocalVariableReference(thisVariable));
    }

    private Call visitStaticReference(String possibleClass, String functionName, List<Argument> arguments) {
        Type classType = scope.resolveClassName(possibleClass);
        FunctionSignature signature = classType.getMethodCallSignature(functionName, arguments);
        return new FunctionCall(signature, arguments, classType);
    }

    private List<Argument> getArgumentsForCall(ArgumentListContext argumentsListCtx) {
        if (argumentsListCtx != null) {
            ArgumentExpressionsListVisitor visitor = new ArgumentExpressionsListVisitor(expressionVisitor);
            return argumentsListCtx.accept(visitor);
        }
        return Collections.emptyList();
    }
}