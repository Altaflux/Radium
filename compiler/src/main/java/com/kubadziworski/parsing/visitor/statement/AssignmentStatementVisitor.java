package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.node.RuleContextElementImpl;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.exception.FinalFieldModificationException;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.PropertyAccessorsUtil;
import org.antlr.v4.runtime.ParserRuleContext;
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


    @Override
    public Statement visitAssignment(@NotNull EnkelParser.AssignmentContext ctx) {

        EnkelParser.ExpressionContext expressionCtx = ctx.postExpr;
        Expression expression = expressionCtx.accept(expressionVisitor);
        String varName = ctx.name().getText();
        if (ctx.preExp != null) {
            return generateAssignment(ctx, ctx.preExp.accept(expressionVisitor), varName, expression);
        }
        if (scope.isLocalVariableExists(varName)) {

//            LocalVariable variable = scope.getLocalVariable(varName);
//            Type varType = variable.getType();
//            if (varType.isPrimitive() && !expression.getType().isNullable().equals(Type.Nullability.NOT_NULL)) {
//                if(varType instanceof TypeProjection){
//                    varType = ((TypeProjection) varType).getInternalType();
//                }
//                Type boxed = ((BoxableType) varType).getBoxedType();
//                variable.changeType(new TypeProjection(boxed, Type.Nullability.NULLABLE));
//            }

            LocalVariable localVariable = scope.getLocalVariable(varName);
            if (!localVariable.isMutable()) {
                throw new FinalFieldModificationException("Cannot modify final variable: " + localVariable.getName());
            }

            return new Assignment(new RuleContextElementImpl(ctx), localVariable, expression);
        } else if (scope.isFieldExists(varName)) {
            return generateAssignment(ctx, new LocalVariableReference(scope.getLocalVariable("this")), varName, expression);
        } else {
            throw new RuntimeException("Assignment on un-declared variable: " + varName);
        }
    }

    private Statement generateAssignment(ParserRuleContext ctx, Expression owner, String varName, Expression expression) {
        Field field;
        //This is only to allow getter and setters field Reference
        if (scope.getClassType().equals(owner.getType())) {
            field = scope.getField(varName);
        } else {
            field = owner.getType().getField(varName);
        }

        //This is only to allow getter and setters field Reference
        if (!field.getName().equals(varName)) {
            return new FieldAssignment(owner, field, expression);
        }

        Optional<FunctionSignature> signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field);
        Optional<FunctionCall> functionCall = signature.map(functionSignature -> {
            ArgumentHolder argument = new ArgumentHolder(expression, null);
            return new PropertyAccessorCall(functionSignature, functionSignature.createArgumentList(Collections.singletonList(argument)), owner, field);
        });

        if (functionCall.isPresent()) {
            return functionCall.get();
        }
        return new FieldAssignment(new RuleContextElementImpl(ctx), owner, field, expression);
    }
}