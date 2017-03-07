package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.*;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.AccessException;
import com.kubadziworski.exception.FinalFieldModificationException;
import com.kubadziworski.exception.IncompatibleTypesException;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.PropertyAccessorsUtil;

import java.util.Optional;

public class AssignmentStatementVisitor extends EnkelParserBaseVisitor<Statement> {
    private final ExpressionVisitor expressionVisitor;
    private final Scope scope;

    public AssignmentStatementVisitor(ExpressionVisitor expressionVisitor, Scope scope) {
        this.expressionVisitor = expressionVisitor;
        this.scope = scope;
    }

    @Override
    public Statement visitAssignment(EnkelParser.AssignmentContext ctx) {
        EnkelParser.ExpressionContext expressionCtx = ctx.postExpr;
        Expression expression = expressionCtx.accept(expressionVisitor);
        String varName = ctx.name().getText();
        if (ctx.preExp != null) {
            Expression owner = ctx.preExp.accept(expressionVisitor);
            Field field = owner.getType().getField(varName);
            validateType(expression, field);
            return new FieldAssignment(new RuleContextElementImpl(ctx), owner, field, expression);
        }
        if (scope.isLocalVariableExists(varName) && scope.getLocalVariable(varName).isVisible()) {
            LocalVariable localVariable = scope.getLocalVariable(varName);
            validateType(expression, localVariable);
            if (!localVariable.isMutable()) {
                throw new FinalFieldModificationException("Cannot modify final variable: " + localVariable.getName());
            }
            return new Assignment(new RuleContextElementImpl(ctx), localVariable, expression);

        } else if (scope.isFieldExists(varName)) {
            Field field = scope.getField(varName);
            if (field.getModifiers().contains(Modifier.FINAL)) {
                throw new FinalFieldModificationException("Cannot modify final field: " + field.getName());
            }
            validateAccessToField(field);
            validateType(expression, field);
            return new FieldAssignment(new RuleContextElementImpl(ctx), new LocalVariableReference(scope.getLocalVariable("this"))
                    , field, expression);
        } else {
            throw new RuntimeException("Assignment on un-declared variable: " + varName);
        }
    }

    private void validateAccessToField(Field field) {
        if (field.getModifiers().contains(Modifier.FINAL)) {
            throw new FinalFieldModificationException("Cannot modify final field: " + field.getName());
        }
        Type classType = scope.getClassType();
        Optional<FunctionSignature> signatureOpt = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field);
        boolean accessThruFunction = signatureOpt
                .map(functionSignature -> !PropertyAccessorsUtil.isFunctionAccessible(functionSignature, classType))
                .orElse(false);

        if (!accessThruFunction && !PropertyAccessorsUtil.isFunctionAccessible(field, classType)) {
            throw new AccessException("Cannot access field: " + field);
        }

    }

    private static void validateType(Expression expression, Variable variable) {
        if (expression.getType().inheritsFrom(variable.getType()) < 0) {
            throw new IncompatibleTypesException(variable.getName(), variable.getType(), expression.getType());
        }
    }
}
