package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ClassDeclarationContext;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.*;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.parsing.FunctionGenerator;
import com.kubadziworski.parsing.visitor.statement.StatementVisitor;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ClassVisitor extends EnkelParserBaseVisitor<ClassDeclaration> {

    private final Scope scope;

    public ClassVisitor(Scope scope) {
        this.scope = scope;
    }

    @Override
    public ClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {

        List<FunctionContext> methodsCtx = ctx.classBody().function();

        FunctionSignature signature = null;
        try {
            signature = scope.getClassType().getConstructorCallSignature(Collections.emptyList());
        } catch (Exception e) {
            //
        }
        boolean defaultConstructorExists = signature != null;
        List<Function> methods = methodsCtx.stream().map(method -> method.accept(new FunctionVisitor(scope)))
                .collect(toList());

        methods.add(generateInitBlocks(ctx));
        FunctionSignature startSignature = null;
        try {
            startSignature = scope.getClassType().getMethodCallSignature("start", Collections.emptyList());
        } catch (Exception e) {
            //
        }
        boolean startMethodDefined = startSignature != null;
        if (startMethodDefined && defaultConstructorExists) {
            methods.add(getGeneratedMainMethod());
        }

        scope.addMethods(methods);
        return new ClassDeclaration(scope.getClassName(), scope.getMetaData().getPackageName(),
                new EnkelType(scope.getFullClassName(), scope), new ArrayList<>(scope.getFields().values()), methods);
    }

    private Constructor generateInitBlocks(ClassDeclarationContext ctx) {
        FunctionSignature signature = scope.getConstructorSignatures().get(0);
        Scope functionScope = new Scope(scope, signature);
        functionScope.addLocalVariable(new LocalVariable("this", functionScope.getClassType()));
        StatementVisitor statementVisitor = new StatementVisitor(functionScope);
        FunctionGenerator functionGenerator = new FunctionGenerator(functionScope);
        functionGenerator.addParametersAsLocalVariables(signature);
        if (!ctx.classBody().initBlock().isEmpty()) {
            EnkelParser.InitBlockContext blockContext = ctx.classBody().initBlock().get(0);

            Block block = (Block) blockContext.block().accept(statementVisitor);
            Block initializersBlock = new Block(block.getScope(), ListUtils.sum(getFieldsInitializers(block.getScope()), block.getStatements()));
            return (Constructor) functionGenerator.generateFunction(signature, initializersBlock, true);
        }
        return getDefaultConstructor(signature);
    }

    private Constructor getDefaultConstructor(FunctionSignature signature) {
        Scope constructorScope = new Scope(scope, signature);
        constructorScope.addLocalVariable(new LocalVariable("this", scope.getClassType(), false));

        FunctionSignature superSignature = scope.getMethodCallSignature(SuperCall.SUPER_IDENTIFIER, Collections.emptyList());
        SuperCall superCall = new SuperCall(superSignature);
        Block block = new Block(constructorScope, ListUtils.sum(Collections.singletonList(superCall), getFieldsInitializers(constructorScope)));
        return new Constructor(signature, block);
    }

    private List<Statement> getFieldsInitializers(Scope scope) {
        return scope.getFields().values().stream()
                .filter(stringFieldEntry -> stringFieldEntry.getInitialExpression().isPresent())
                .map(field -> new FieldAssignment(new LocalVariableReference(scope.getLocalVariable("this")), field,
                        field.getInitialExpression().get())).collect(Collectors.toList());
    }

    private Function getGeneratedMainMethod() {
        Parameter args = new Parameter("args", BuiltInType.STRING_ARR, null);
        Type owner = scope.getClassType();
        FunctionSignature functionSignature = new FunctionSignature("main", Collections.singletonList(args), VoidType.INSTANCE,
                Modifiers.empty().with(Modifier.PUBLIC).with(Modifier.STATIC), owner);

        FunctionSignature constructorCallSignature = owner.getConstructorCallSignature(Collections.emptyList());
        ConstructorCall constructorCall = new ConstructorCall(constructorCallSignature, scope.getClassType());
        FunctionSignature startFunSignature = new FunctionSignature("start", Collections.emptyList(), VoidType.INSTANCE, Modifiers.empty().with(Modifier.PUBLIC), owner);
        FunctionCall startFunctionCall = new FunctionCall(startFunSignature, Collections.emptyList(), scope.getClassType());
        Block block = new Block(new Scope(scope), Arrays.asList(constructorCall, startFunctionCall));
        return new Function(functionSignature, block);
    }

}
