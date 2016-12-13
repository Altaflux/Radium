package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelBaseVisitor;
import com.kubadziworski.antlr.EnkelParser.ClassDeclarationContext;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.ConstructorCall;
import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.node.statement.VariableDeclaration;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import org.antlr.v4.runtime.misc.NotNull;
import org.apache.commons.collections4.ListUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by kuba on 01.04.16.
 */
public class ClassVisitor extends EnkelBaseVisitor<ClassDeclaration> {

    private final Scope scope;

    public ClassVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public ClassDeclaration visitClassDeclaration(@NotNull ClassDeclarationContext ctx) {

        List<FunctionContext> methodsCtx = ctx.classBody().function();

        FunctionSignature signature = null;
        try {
            signature = scope.getClassType().getConstructorCallSignature(Collections.emptyList());
        } catch (Exception e) {
            //
        }
        boolean defaultConstructorExists = signature != null;
        addDefaultConstructorSignatureToScope(scope.getFullClassName(), defaultConstructorExists);
        List<Function> methods = methodsCtx.stream()
                .map(method -> {
                    //TODO DONT PROCESS FIELDS IF THIS() exists

                    Function function = method.accept(new FunctionVisitor(scope));
                    if (function instanceof Constructor) {
                        Block block = (Block) function.getRootStatement();
                        //Check for first statement TODO
                        Block block1 = new Block(block.getScope(), ListUtils.sum(getFieldsInitializers(), block.getStatements()));
                        return new Constructor(function.getFunctionSignature(), block1);
                    }

                    return method.accept(new FunctionVisitor(scope));
                })
                .collect(toList());
        if (!defaultConstructorExists) {
            methods.add(getDefaultConstructor());
        }

        FunctionSignature startSignature = null;
        try {
            startSignature = scope.getClassType().getMethodCallSignature("start", Collections.emptyList());
        } catch (Exception e) {
            //
        }
        boolean startMethodDefined = startSignature != null;
        if (startMethodDefined) {
            methods.add(getGeneratedMainMethod());
        }
        scope.getFields().values().stream()
                .peek(field -> methods.add(field.getGetterFunction()))
                .forEach(field -> methods.add(field.getSetterFunction()));


        return new ClassDeclaration(scope.getClassName(), new EnkelType(scope.getFullClassName(), scope), new ArrayList<>(scope.getFields().values()), methods);
    }

    private void addDefaultConstructorSignatureToScope(String name, boolean defaultConstructorExists) {
        if (!defaultConstructorExists) {
            FunctionSignature constructorSignature = new FunctionSignature(name, Collections.emptyList(), VoidType.INSTANCE, Modifier.PUBLIC, scope.getClassType());
            scope.addConstructor(constructorSignature);
        }
    }

    private Constructor getDefaultConstructor() {
        FunctionSignature signature = scope.getClassType().getConstructorCallSignature(Collections.emptyList());
        Block block = new Block(scope, getFieldsInitializers());
        return new Constructor(signature, block);
    }

    private List<Statement> getFieldsInitializers() {
        return scope.getFields().values().stream()
                .filter(stringFieldEntry -> stringFieldEntry.getInitialExpression().isPresent())
                .map(field -> {
                    VariableDeclaration declaration = new VariableDeclaration(field.getName(), field.getInitialExpression().get(), field.getType(), false);
                    return new Assignment(declaration, true);
                }).collect(Collectors.toList());
    }

    private Function getGeneratedMainMethod() {
        Parameter args = new Parameter("args", BuiltInType.STRING_ARR, null);
        Type owner = scope.getClassType();
        FunctionSignature functionSignature = new FunctionSignature("main", Collections.singletonList(args), VoidType.INSTANCE, Modifier.PUBLIC + Modifier.STATIC, owner);

        FunctionSignature constructorCallSignature = owner.getConstructorCallSignature(Collections.emptyList());
        ConstructorCall constructorCall = new ConstructorCall(constructorCallSignature, scope.getFullClassName());
        FunctionSignature startFunSignature = new FunctionSignature("start", Collections.emptyList(), VoidType.INSTANCE, Modifier.PUBLIC, owner);
        FunctionCall startFunctionCall = new FunctionCall(startFunSignature, Collections.emptyList(), scope.getClassType());
        Block block = new Block(new Scope(scope), Arrays.asList(constructorCall, startFunctionCall));
        return new Function(functionSignature, block);
    }

}
