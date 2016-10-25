package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.ReflectionUtils;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Collections;
import java.util.Optional;

public class AssignmentStatementVisitor extends EnkelBaseVisitor<Statement> {
    private final ExpressionVisitor expressionVisitor;
    private final Scope scope;

    public AssignmentStatementVisitor(ExpressionVisitor expressionVisitor, Scope scope) {
        this.expressionVisitor = expressionVisitor;
        this.scope = scope;
    }

//    @Override
//    public Statement visitAssignment(@NotNull EnkelParser.AssignmentContext ctx) {
//
//        EnkelParser.ExpressionContext expressionCtx = ctx.postExpr;
//        Expression expression = expressionCtx.accept(expressionVisitor);
//        String varName = ctx.name().getText();
//        if (ctx.preExp != null) {
//            return new Assignment(ctx.preExp.accept(expressionVisitor), varName, expression);
//        }
//        if (scope.isLocalVariableExists(varName)) {
//            return new Assignment(varName, expression);
//        } else if (scope.isFieldExists(varName)) {
//            return new Assignment(new LocalVariableReference(scope.getLocalVariable("this")), varName, expression);
//        } else {
//            throw new RuntimeException("Assignment on un-declared variable: " + varName);
//        }
//    }

    @Override
    public Statement visitAssignment(@NotNull EnkelParser.AssignmentContext ctx) {

        EnkelParser.ExpressionContext expressionCtx = ctx.postExpr;
        Expression expression = expressionCtx.accept(expressionVisitor);
        String varName = ctx.name().getText();
        if (ctx.preExp != null) {
            return generateAssignment(ctx.preExp.accept(expressionVisitor), varName, expression);
        }
        if (scope.isLocalVariableExists(varName)) {
            return new Assignment(varName, expression);
        } else if (scope.isFieldExists(varName)) {
            return generateAssignment(new LocalVariableReference(scope.getLocalVariable("this")), varName, expression);
        } else {
            throw new RuntimeException("Assignment on un-declared variable: " + varName);
        }
    }

    private Statement generateAssignment(Expression owner, String varName, Expression expression) {
        Field field = scope.getField(owner.getType(), varName);

        Optional<FunctionSignature> signature = ReflectionUtils.getSetterFunctionSignatureForField(field);
        Optional<FunctionCall> functionCall = signature.map(functionSignature -> {
            Argument argument = new Argument(expression, Optional.empty());
            return new FunctionCall(functionSignature, Collections.singletonList(argument), owner);
        });

        if (functionCall.isPresent()) {
            return functionCall.get();
        }
        return new Assignment(owner, varName, expression);
    }
}