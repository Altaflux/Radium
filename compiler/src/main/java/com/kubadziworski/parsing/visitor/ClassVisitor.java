package com.kubadziworski.parsing.visitor;

import com.kubadziworski.antlr.EnkelParser;
import com.kubadziworski.antlr.EnkelParser.ClassDeclarationContext;
import com.kubadziworski.antlr.EnkelParser.FunctionContext;
import com.kubadziworski.antlr.EnkelParserBaseVisitor;
import com.kubadziworski.domain.*;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.function.ConstructorCall;
import com.kubadziworski.domain.node.expression.function.FunctionCall;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.node.expression.function.SuperCall;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.BuiltInType;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.parsing.FunctionGenerator;
import com.kubadziworski.parsing.visitor.expression.ExpressionVisitor;
import com.kubadziworski.parsing.visitor.expression.function.ArgumentExpressionsListVisitor;
import com.kubadziworski.parsing.visitor.statement.StatementVisitor;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private Statement generateSuperCall(ClassDeclarationContext classDeclarationContext, FunctionSignature signature) {
        FunctionScope superScope = new FunctionScope(this.scope, signature);
        //We do not make local variables invisible in this case
        signature.getParameters()
                .forEach(param -> superScope.addLocalVariable(new LocalVariable(param.getName(), param.getType())));

        EnkelParser.AbstractClassAndInterfacesContext ctx = classDeclarationContext.abstractClassAndInterfaces();
        if (ctx != null && ctx.abstractClassInit() != null) {
            ExpressionVisitor expressionVisitor = new ExpressionVisitor(superScope);
            EnkelParser.ArgumentListContext listContext = ctx.abstractClassInit().argumentList();
            ArgumentExpressionsListVisitor visitor = new ArgumentExpressionsListVisitor(expressionVisitor);
            List<ArgumentHolder> argumentHolders = listContext.accept(visitor);
            FunctionSignature superSignature = superScope.getSuperClassType().getConstructorCallSignature(argumentHolders);
            return new SuperCall(superSignature, superSignature.createArgumentList(argumentHolders));
        } else {
            FunctionSignature superSignature = superScope.getSuperClassType().getConstructorCallSignature(Collections.emptyList());
            return new SuperCall(superSignature);
        }
    }

    private Constructor generateInitBlocks(ClassDeclarationContext ctx) {
        FunctionSignature signature = scope.getConstructorSignatures().get(0);
        FunctionScope functionScope = new FunctionScope(scope, signature);
        functionScope.addLocalVariable(new LocalVariable("this", functionScope.getClassType()));
        StatementVisitor statementVisitor = new StatementVisitor(functionScope);
        FunctionGenerator functionGenerator = new FunctionGenerator(functionScope);
        functionGenerator.addParametersAsLocalVariables(signature);

        if (!ctx.classBody().initBlock().isEmpty()) {
            EnkelParser.InitBlockContext blockContext = ctx.classBody().initBlock().get(0);

            Block block = (Block) blockContext.block().accept(statementVisitor);
            Statement superCall = generateSuperCall(ctx, signature);
            List<Statement> statements = ListUtils.sum(getFieldsInitializers(block.getScope()), block.getStatements());
            Block initializersBlock = new Block(block.getScope(), ListUtils.sum(Collections.singletonList(superCall), statements));
            return (Constructor) functionGenerator.generateFunction(signature, initializersBlock, true);
        }
        return getDefaultConstructor(signature, ctx);
    }


    private Constructor getDefaultConstructor(FunctionSignature signature, ClassDeclarationContext ctx) {
        FunctionScope constructorScope = new FunctionScope(scope, signature);
        constructorScope.addLocalVariable(new LocalVariable("this", scope.getClassType(), false));
        FunctionGenerator functionGenerator = new FunctionGenerator(constructorScope);
        functionGenerator.addParametersAsLocalVariables(signature);

        Statement superCall = generateSuperCall(ctx, signature);
        Block block = new Block(constructorScope, ListUtils.sum(Collections.singletonList(superCall), getFieldsInitializers(constructorScope)));
        return new Constructor(signature, block);
    }

    private List<Statement> getFieldsInitializers(FunctionScope scope) {
        return scope.getScope().getFields().values().stream()
                .filter(stringFieldEntry -> stringFieldEntry.getInitialExpression().isPresent())
                .map(field -> field.getInitialExpression().get().supply(scope)).collect(toList());
    }

    private Function getGeneratedMainMethod() {
        Parameter args = new Parameter("args", BuiltInType.STRING_ARR, null);
        Type owner = scope.getClassType();
        FunctionSignature functionSignature = new FunctionSignature("main", Collections.singletonList(args), VoidType.INSTANCE,
                Modifiers.empty().with(Modifier.PUBLIC).with(Modifier.STATIC), owner, SignatureType.FUNCTION_CALL);

        FunctionSignature constructorCallSignature = owner.getConstructorCallSignature(Collections.emptyList());
        ConstructorCall constructorCall = new ConstructorCall(constructorCallSignature, scope.getClassType());
        FunctionSignature startFunSignature = new FunctionSignature("start", Collections.emptyList(), VoidType.INSTANCE,
                Modifiers.empty().with(Modifier.PUBLIC), owner, SignatureType.FUNCTION_CALL);
        FunctionCall startFunctionCall = new FunctionCall(startFunSignature, Collections.emptyList(), new EmptyExpression(scope.getClassType()));
        Block block = new Block(new FunctionScope(scope, functionSignature), Arrays.asList(constructorCall, startFunctionCall));
        return new Function(functionSignature, block);
    }

}
