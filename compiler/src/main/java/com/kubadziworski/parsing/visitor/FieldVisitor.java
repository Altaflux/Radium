package com.kubadziworski.parsing.visitor;


import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.CompilationException;
import com.kubadziworski.parsing.FunctionGenerator;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.util.PropertyAccessorsUtil;
import com.kubadziworski.util.TypeResolver;

import java.util.Collections;
import java.util.HashSet;
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


        Modifiers modifiersSet = new Modifiers(Collections.emptySet());
        if (ctx.fieldModifier() != null && ctx.fieldModifier().accessModifiers() != null) {
            switch (ctx.fieldModifier().accessModifiers().getText()) {
                case "public": {
                    modifiersSet = modifiersSet.with(Modifier.PUBLIC);
                    break;
                }
                case "protected": {
                    modifiersSet = modifiersSet.with(Modifier.PUBLIC);
                    break;
                }
                case "private": {
                    modifiersSet = modifiersSet.with(Modifier.PUBLIC);
                    break;
                }
            }
        } else {
            modifiersSet = modifiersSet.with(Modifier.PUBLIC);
        }

        Set<Modifier> mSets = new HashSet<>();
        if (ctx.fieldModifier() != null) {
            mSets = ctx.fieldModifier().fieldModifiers().stream().map(methodModifiersContext -> {
                if (methodModifiersContext.getText().equals("static")) {
                    return Modifier.STATIC;
                }
                if (methodModifiersContext.getText().equals("final")) {
                    return com.kubadziworski.domain.Modifier.FINAL;
                }
                throw new CompilationException("");
            }).collect(Collectors.toSet());

        }
        for (Modifier md : mSets) {
            modifiersSet = modifiersSet.with(md);
        }

        Field field;
        ExpressionVisitor statementVisitor = new ExpressionVisitor(scope);
        if (ctx.expression() != null) {
            Expression expression = ctx.expression().accept(statementVisitor);
            field = new Field(name, owner, type, expression, modifiersSet);
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
            field.setSetterFunction(generateSetter(field));
        }

        if (ctx.getter() != null) {
            FunctionSignature signature = PropertyAccessorsUtil.createGetterForField(field);
            Scope functionScope = new Scope(scope, signature);
            functionScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
            functionScope.addField("field", field);

            FunctionGenerator generator = new FunctionGenerator(functionScope);
            field.setGetterFunction(generator.generateFunction(signature, ctx.getter().functionContent(), false));
        } else {
            field.setGetterFunction(generateGetter(field));
        }

        return field;
    }

    private Function generateGetter(Field field) {
        FunctionSignature getter = PropertyAccessorsUtil.createGetterForField(field);
        Scope newScope = new Scope(this.scope);
        newScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        FieldReference fieldReference = new FieldReference(field, new LocalVariableReference(newScope.getLocalVariable("this")));
        ReturnStatement returnStatement = new ReturnStatement(fieldReference);
        Block block = new Block(newScope, Collections.singletonList(returnStatement));
        return new Function(getter, block);
    }

    private Function generateSetter(Field field) {
        FunctionSignature getter = PropertyAccessorsUtil.createSetterForField(field);
        Scope newScope = new Scope(this.scope);
        newScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        getter.getParameters()
                .forEach(param -> newScope.addLocalVariable(new LocalVariable(param.getName(), param.getType())));
        LocalVariableReference localVariableReference = new LocalVariableReference(new LocalVariable(field.getName(), field.getType()));
        LocalVariableReference thisReference = new LocalVariableReference(newScope.getLocalVariable("this"));

        FieldAssignment assignment = new FieldAssignment(thisReference, field, localVariableReference);
        Block block = new Block(newScope, Collections.singletonList(assignment));
        return new Function(getter, block);
    }
}
