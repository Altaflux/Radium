package com.kubadziworski.parsing.visitor.expression.function;

import com.kubadziworski.antlr.EnkelParser.ArgumentListContext;
import com.kubadziworski.antlr.EnkelParser.ConstructorCallContext;
import com.kubadziworski.antlr.EnkelParser.FunctionCallContext;
import com.kubadziworski.antlr.EnkelParser.SupercallContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.function.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.*;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.PropertyAccessorsUtil;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collections;
import java.util.List;

import static com.kubadziworski.domain.node.expression.function.SuperCall.SUPER_IDENTIFIER;

public class CallExpressionVisitor extends EnkelParserBaseVisitor<Call> {

    private final CallExpressionVisitorImp callExpressionVisitorImp;
    private final Scope scope;

    public CallExpressionVisitor(ExpressionVisitor expressionVisitor, Scope scope) {
        callExpressionVisitorImp = new CallExpressionVisitorImp(expressionVisitor, scope);
        this.scope = scope;
    }

    @Override
    public Call visitFunctionCall(FunctionCallContext ctx) {
        Call call = callExpressionVisitorImp.visitFunctionCall(ctx);
        validateAccessToFunction(call.getFunctionSignature());
        return call;
    }

    @Override
    public Call visitConstructorCall(ConstructorCallContext ctx) {
        Call call = callExpressionVisitorImp.visitConstructorCall(ctx);
        validateAccessToFunction(call.getFunctionSignature());
        return call;
    }

    @Override
    public Call visitSupercall(SupercallContext ctx) {
        Call call = callExpressionVisitorImp.visitSupercall(ctx);
        validateAccessToFunction(call.getFunctionSignature());
        return call;
    }


    private static class CallExpressionVisitorImp extends EnkelParserBaseVisitor<Call> {
        private final ExpressionVisitor expressionVisitor;
        private final Scope scope;

        CallExpressionVisitorImp(ExpressionVisitor expressionVisitor, Scope scope) {
            this.expressionVisitor = expressionVisitor;
            this.scope = scope;
        }

        @Override
        public Call visitFunctionCall(FunctionCallContext ctx) {
            String functionName = ctx.functionName().getText();
            if (functionName.equals(scope.getFullClassName())) {
                throw new FunctionNameEqualClassException(functionName);
            }
            List<ArgumentHolder> arguments = getArgumentsForCall(ctx.argumentList());

            if (ctx.ConstructorDelegationCall_super() != null) {
                return createSuperFunctionCall(ctx, functionName, arguments);
            }


            boolean ownerIsExplicit = ctx.owner != null;
            if (ownerIsExplicit) {
                try {
                    Expression owner = ctx.owner.accept(expressionVisitor);
                    FunctionSignature signature = owner.getType().getMethodCallSignature(functionName, arguments);
                    if (signature.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC)) {
                        //If the reference is static we can avoid calling the owning reference
                        //and simply use the class to call it.
                        //We may need to check if this doesn't causes trouble, else we use a POP after
                        //calling the owner expression, for now lets not optimize...
                        return new FunctionCall(new RuleContextElementImpl(ctx), signature, signature.createArgumentList(arguments), new PopExpression(owner));
                    }
                    return new FunctionCall(new RuleContextElementImpl(ctx), signature, signature.createArgumentList(arguments), owner);
                } catch (ClassNotFoundForNameException | FieldNotFoundException e) {
                    String possibleClass = ctx.owner.getText();
                    return visitStaticReference(ctx, possibleClass, functionName, arguments);
                }
            }

            FunctionSignature signature = scope.getMethodCallSignature(functionName, arguments);
            if (scope.getCurrentFunctionSignature().equals(signature) && signature.getModifiers().contains(com.kubadziworski.domain.Modifier.INLINE)) {
                throw new CompilationException("Inline function '" + signature.getName() + "' cannot be recursive");
            }
            if (signature.getModifiers().contains(com.kubadziworski.domain.Modifier.STATIC)) {
                return new FunctionCall(new RuleContextElementImpl(ctx), signature, signature.createArgumentList(arguments), new EmptyExpression(signature.getOwner()));
            }

            Type thisType = new EnkelType(scope.getFullClassName(), scope);
            LocalVariable thisVariable = new LocalVariable("this", thisType);
            return new FunctionCall(new RuleContextElementImpl(ctx), signature, signature.createArgumentList(arguments), new LocalVariableReference(thisVariable));
        }

        @Override
        public Call visitConstructorCall(ConstructorCallContext ctx) {
            Type className = scope.resolveClassName(ctx.typeName().getText());
            List<ArgumentHolder> arguments = getArgumentsForCall(ctx.argumentList());
            FunctionSignature signature = className.getConstructorCallSignature(arguments);
            return new ConstructorCall(new RuleContextElementImpl(ctx), signature, className, signature.createArgumentList(arguments));
        }

        @Override
        public Call visitSupercall(SupercallContext ctx) {
            List<ArgumentHolder> arguments = getArgumentsForCall(ctx.argumentList());
            FunctionSignature signature = scope.getMethodCallSignature(SUPER_IDENTIFIER, arguments);
            return new SuperCall(new RuleContextElementImpl(ctx), signature, signature.createArgumentList(arguments));
        }

        private FunctionCall createSuperFunctionCall(FunctionCallContext ctx, String functionName, List<ArgumentHolder> arguments) {

            FunctionSignature signature = scope.getSuperClassType().getMethodCallSignature(functionName, arguments);
            Type thisType = new EnkelType(scope.getFullClassName(), scope);
            LocalVariable thisVariable = new LocalVariable("this", thisType);
            return new FunctionCall(new RuleContextElementImpl(ctx), signature, signature.createArgumentList(arguments), new LocalVariableReference(thisVariable), CallType.SUPER_CALL);
        }

        private Call visitStaticReference(ParserRuleContext ctx, String possibleClass, String functionName, List<ArgumentHolder> arguments) {
            Type classType = scope.resolveClassName(possibleClass);
            FunctionSignature signature = classType.getMethodCallSignature(functionName, arguments);
            return new FunctionCall(new RuleContextElementImpl(ctx), signature, signature.createArgumentList(arguments), new EmptyExpression(classType));
        }

        private List<ArgumentHolder> getArgumentsForCall(ArgumentListContext argumentsListCtx) {
            if (argumentsListCtx != null) {
                ArgumentExpressionsListVisitor visitor = new ArgumentExpressionsListVisitor(expressionVisitor);
                return argumentsListCtx.accept(visitor);
            }
            return Collections.emptyList();
        }
    }

    private void validateAccessToFunction(FunctionSignature functionSignature) {
        Type classType = scope.getClassType();
        if (!PropertyAccessorsUtil.isFunctionAccessible(functionSignature, classType)) {
            throw new AccessException("Cannot call method: " + functionSignature);
        }
    }
}
