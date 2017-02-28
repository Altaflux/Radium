package com.kubadziworski.parsing.visitor;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.IncompatibleTypesException;
import com.kubadziworski.parsing.FunctionGenerator;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.PropertyAccessorsUtil;
import com.kubadziworski.util.TypeResolver;

import java.util.Set;
import java.util.stream.Collectors;

public class FieldVisitor extends EnkelParserBaseVisitor<Field> {

    private final Scope scope;

    public FieldVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Field visitField(EnkelParser.FieldContext ctx) {
        Type owner = scope.getClassType();
        Type type = TypeResolver.getFromTypeContext(ctx.type(), scope);
        String name = ctx.name().getText();


        Modifiers modifiersSet = Modifiers.empty();
        if (ctx.fieldModifier() != null) {
            Set<Modifier> mSets = ctx.fieldModifier().fieldModifiers().stream()
                    .map(methodModifiersContext -> Modifier.fromValue(methodModifiersContext.getText())
                    ).collect(Collectors.toSet());
            for (Modifier md : mSets) {
                modifiersSet = modifiersSet.with(md);
            }
            if (ctx.fieldModifier().accessModifiers() != null) {
                modifiersSet = modifiersSet.with(Modifier.fromValue(ctx.fieldModifier().accessModifiers().getText()));
            } else {
                modifiersSet = modifiersSet.with(Modifier.PUBLIC);
            }
        }

        if (ctx.KEYWORD_val() != null) {
            modifiersSet = modifiersSet.with(Modifier.FINAL);
        }

        Field field;
        ExpressionVisitor statementVisitor = new ExpressionVisitor(scope);
        if (ctx.expression() != null) {
            Expression expression = ctx.expression().accept(statementVisitor);
            validateType(expression, type);
            field = new Field(name, owner, type, modifiersSet, field1 -> scope ->
                    new FieldAssignment(new LocalVariableReference(scope.getLocalVariable("this")), field1, expression));
        } else {
            field = new Field(name, owner, type, modifiersSet);
        }


        if (ctx.setter() != null) {
            String fieldName = ctx.setter().SimpleName().getText();
            FunctionSignature signature = PropertyAccessorsUtil.createSetterForField(field, fieldName);
            Scope functionScope = new Scope(scope, signature);
            functionScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
            functionScope.addField("field", field);

            FunctionGenerator generator = new FunctionGenerator(functionScope);
            field.setSetterFunction(generator.generateFunction(signature, ctx.setter().block(), false));
        } else {
            field.setSetterFunction(PropertyAccessorsUtil.generateSetter(field, scope));
        }

        if (ctx.getter() != null) {
            FunctionSignature signature = PropertyAccessorsUtil.createGetterForField(field);
            Scope functionScope = new Scope(scope, signature);
            functionScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
            functionScope.addField("field", field);

            FunctionGenerator generator = new FunctionGenerator(functionScope);
            field.setGetterFunction(generator.generateFunction(signature, ctx.getter().functionContent(), false));
        } else {
            field.setGetterFunction(PropertyAccessorsUtil.generateGetter(field, scope));
        }

        return field;
    }

    private static void validateType(Expression expression, Type targetType) {
        if (expression.getType().inheritsFrom(targetType) < 0) {
            throw new IncompatibleTypesException(targetType.getName(), targetType, expression.getType());
        }
    }

}
