package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.LocalVariableReference;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.WrongModifiersException;
import com.kubadziworski.parsing.visitor.statement.StatementVisitor;
import com.kubadziworski.util.PropertyAccessorsUtil;
import com.kubadziworski.util.TypeResolver;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Created by kuba on 13.05.16.
 */
public class FieldVisitor extends EnkelBaseVisitor<Field> {

    private final Scope scope;

    public FieldVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public Field visitField(@NotNull EnkelParser.FieldContext ctx) {
        Type owner = scope.getClassType();
        Type type = TypeResolver.getFromTypeContext(ctx.type(), scope);
        String name = ctx.name().getText();


        int modifiers = Modifier.PUBLIC;
        if (ctx.fieldModifier() != null && ctx.fieldModifier().size() > 1) {
            throw new WrongModifiersException("Too many modifiers: " + ctx.fieldModifier());
        }
        if (ctx.fieldModifier() != null) {
            modifiers = ctx.fieldModifier().stream().map(methodModifiersContext -> {
                if (methodModifiersContext.getText().equals("private")) {
                    return Modifier.PRIVATE;
                }
                if (methodModifiersContext.getText().equals("protected")) {
                    return Modifier.PROTECTED;
                }
                if (methodModifiersContext.getText().equals("public")) {
                    return Modifier.PUBLIC;
                }
                return 0;
            }).findAny().orElse(Modifier.PUBLIC);
        }
        Field field = new Field(name, owner, type, modifiers);

        if (ctx.setter() != null) {
            String fieldName = ctx.setter().parameter().ID().getText();
            FunctionSignature signature = PropertyAccessorsUtil.createSetterForField(field, fieldName);
            Scope functionScope = new Scope(scope);
            functionScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
            functionScope.addField("field", field);
            addParametersAsLocalVariables(signature, functionScope);
            Block block = getBlock(ctx.setter().block(), functionScope);
            field.setSetterFunction(new Function(signature, block));
        } else {
            field.setSetterFunction(generateSetter(field));
        }

        if (ctx.getter() != null) {
            FunctionSignature signature = PropertyAccessorsUtil.createGetterForField(field);
            Scope functionScope = new Scope(scope);
            functionScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
            functionScope.addField("field", field);
            addParametersAsLocalVariables(signature, functionScope);
            Block block = getBlock(ctx.getter().block(), functionScope);
            field.setGetterFunction(new Function(signature, block));
        } else {
            field.setGetterFunction(generateGetter(field));
        }

        return field;
    }

    private void addParametersAsLocalVariables(FunctionSignature signature, Scope scope) {
        signature.getParameters()
                .forEach(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())));
    }

    private Block getBlock(EnkelParser.BlockContext block, Scope scope) {
        StatementVisitor statementVisitor = new StatementVisitor(scope);
        return (Block) block.accept(statementVisitor);
    }


    private Function generateGetter(Field field) {
        FunctionSignature getter = PropertyAccessorsUtil.createGetterForField(field);
        Scope newScope = new Scope(this.scope);
        newScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        newScope.addField(field);
        FieldReference fieldReference = new FieldReference(field, new LocalVariableReference(newScope.getLocalVariable("this")));
        ReturnStatement returnStatement = new ReturnStatement(fieldReference);
        Block block = new Block(newScope, Collections.singletonList(returnStatement));
        return new Function(getter, block);
    }

    private Function generateSetter(Field field) {
        FunctionSignature getter = PropertyAccessorsUtil.createSetterForField(field);
        Scope newScope = new Scope(this.scope);
        newScope.addField(field);
        newScope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        addParametersAsLocalVariables(getter, newScope);
        LocalVariableReference localVariableReference = new LocalVariableReference(new LocalVariable(field.getName(), field.getType()));
        LocalVariableReference thisReference = new LocalVariableReference(newScope.getLocalVariable("this"));

        Assignment assignment = new Assignment(thisReference, field.getName(), localVariableReference);
        Block block = new Block(newScope, Collections.singletonList(assignment));
        return new Function(getter, block);
    }
}
