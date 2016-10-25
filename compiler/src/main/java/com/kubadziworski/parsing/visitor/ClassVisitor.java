package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ClassDeclarationContext;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PropertyAccessorsUtil;
import org.antlr.v4.runtime.misc.NotNull;

import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by kuba on 01.04.16.
 */
class ClassVisitor extends EnkelBaseVisitor<ClassDeclaration> {

    private final Scope scope;

    ClassVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public ClassDeclaration visitClassDeclaration(@NotNull ClassDeclarationContext ctx) {

        List<FunctionContext> methodsCtx = ctx.classBody().function();

        boolean defaultConstructorExists = scope.isParameterLessSignatureExists(scope.getFullClassName());
        addDefaultConstructorSignatureToScope(scope.getFullClassName(), defaultConstructorExists);
        List<Function> methods = methodsCtx.stream()
                .map(method -> method.accept(new FunctionVisitor(scope)))
                .collect(toList());
        if (!defaultConstructorExists) {
            methods.add(getDefaultConstructor());
        }
        boolean startMethodDefined = scope.isParameterLessSignatureExists("start");
        if (startMethodDefined) {
            methods.add(getGeneratedMainMethod());
        }
        scope.getFields().values().stream()
                .peek(field -> methods.add(generateGetter(field)))
                .forEach(field -> methods.add(generateSetter(field)));

        return new ClassDeclaration(scope.getClassName(), new ClassType(scope.getFullClassName()), new ArrayList<>(scope.getFields().values()), methods);
    }

    private void addDefaultConstructorSignatureToScope(String name, boolean defaultConstructorExists) {
        if (!defaultConstructorExists) {
            FunctionSignature constructorSignature = new FunctionSignature(name, Collections.emptyList(), BultInType.VOID, Modifier.PUBLIC, scope.getClassType());
            scope.addSignature(constructorSignature);
        }
    }

    private Function generateGetter(Field field) {
        FunctionSignature getter = PropertyAccessorsUtil.createGetterForField(field);
        Scope scope = new Scope(this.scope);
        scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));

        FieldReference fieldReference = new FieldReference(field, new LocalVariableReference(scope.getLocalVariable("this")));
        ReturnStatement returnStatement = new ReturnStatement(fieldReference);
        Block block = new Block(scope, Collections.singletonList(returnStatement));
        return new Function(getter, block);
    }

    private Function generateSetter(Field field) {
        FunctionSignature getter = PropertyAccessorsUtil.createSetterForField(field);
        Scope scope = new Scope(this.scope);
        scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        addParametersAsLocalVariables(getter, scope);
        LocalVariableReference localVariableReference = new LocalVariableReference(new LocalVariable(field.getName(), field.getType()));
        LocalVariableReference thisReference = new LocalVariableReference(scope.getLocalVariable("this"));

        Assignment assignment = new Assignment(thisReference, field.getName(), localVariableReference);
        Block block = new Block(scope, Collections.singletonList(assignment));
        return new Function(getter, block);
    }

    private void addParametersAsLocalVariables(FunctionSignature signature, Scope scope) {
        signature.getParameters()
                .forEach(param -> scope.addLocalVariable(new LocalVariable(param.getName(), param.getType())));
    }

    private Constructor getDefaultConstructor() {
        FunctionSignature signature = scope.getMethodCallSignatureWithoutParameters(scope.getFullClassName());
        return new Constructor(signature, Block.empty(scope));
    }

    private Function getGeneratedMainMethod() {
        Parameter args = new Parameter("args", BultInType.STRING_ARR, null);
        Type owner = scope.getClassType();
        FunctionSignature functionSignature = new FunctionSignature("main", Collections.singletonList(args), BultInType.VOID, Modifier.PUBLIC + Modifier.STATIC, owner);
        ConstructorCall constructorCall = new ConstructorCall(scope.getFullClassName());
        FunctionSignature startFunSignature = new FunctionSignature("start", Collections.emptyList(), BultInType.VOID, Modifier.PUBLIC, owner);
        FunctionCall startFunctionCall = new FunctionCall(startFunSignature, Collections.emptyList(), scope.getClassType());
        Block block = new Block(new Scope(scope), Arrays.asList(constructorCall, startFunctionCall));
        return new Function(functionSignature, block);
    }

}
