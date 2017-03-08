package com.kubadziworski.bytecodegeneration.util.defaultmethod;

import com.kubadziworski.bytecodegeneration.MethodGenerator;
import com.kubadziworski.bytecodegeneration.intrinsics.IntrinsicMethodCaller;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.bytecodegeneration.util.ModifierTransformer;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.expression.function.FunctionCall;
import com.kubadziworski.domain.node.expression.function.SignatureType;
import com.kubadziworski.domain.node.expression.function.SuperCall;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.IfStatement;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.EnkelType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.parsing.FunctionGenerator;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class DefaultMethodHandler {


    private static Type DEFAULT_MARKER = ClassTypeFactory.createClassType("radium.internal.DefaultConstructorMarker");

    FunctionSignature getDefaultSignature(FunctionSignature signature) {
        return new FunctionSignature(signature.getName() + "$default", getDefaultMethodParameters(signature), signature.getReturnType(),
                signature.getModifiers(), signature.getOwner(), SignatureType.FUNCTION_CALL);
    }

    FunctionSignature getDefaultSignatureForConstructor(FunctionSignature signature) {
        return new FunctionSignature(signature.getName(), getDefaultMethodParameters(signature), signature.getReturnType(),
                signature.getModifiers(), signature.getOwner(), SignatureType.FUNCTION_CALL);
    }

    public void createSyntheticForConstructor(Constructor function, ClassVisitor cv, MethodGenerator methodGenerator) {
        boolean shouldCreate = function.getParameters().stream().anyMatch(parameter -> parameter.getDefaultValue().isPresent());
        if (!shouldCreate) {
            return;
        }

        FunctionSignature defaultSignature = getDefaultSignatureForConstructor(function.getFunctionSignature());
        String description = DescriptorFactory.getMethodDescriptor(defaultSignature);
        int mod = ModifierTransformer.transform(function.getModifiers().with(Modifier.SYNTHETIC));
        MethodVisitor mvs = cv.visitMethod(mod, "<init>", description, null, null);
        InstructionAdapter adapter = new InstructionAdapter(mvs);

        StatementGenerator statementGenerator = statementGenerator(function, adapter);
        FunctionScope scope = statementGenerator.getScope();
        Expression owner = scope.isLocalVariableExists("this") ? new LocalVariableReference(scope.getLocalVariable("this"))
                : new EmptyExpression(scope.getClassType());
        List<Argument> functionArguments = verifyMissingArguments(function, statementGenerator);

        owner.accept(statementGenerator);
        functionArguments.forEach(argument1 -> argument1.accept(statementGenerator));
        String ownerDescriptor = function.getFunctionSignature().getOwner().getAsmType().getInternalName();
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(function.getFunctionSignature());
        mvs.visitMethodInsn(Opcodes.INVOKESPECIAL, ownerDescriptor, "<init>", methodDescriptor, false);
        ReturnStatement returnStatement = new ReturnStatement(new EmptyExpression(VoidType.INSTANCE));
        returnStatement.accept(statementGenerator);

        adapter.visitMaxs(-1, -1);
        adapter.visitEnd();

        boolean createEmptyConstructor = function.getParameters().stream()
                .allMatch(parameter -> parameter.getDefaultValue().isPresent());
        if (createEmptyConstructor) {
            createParameterLessConstructor(function, methodGenerator);
        }
    }


    public void createSyntheticForFunction(Function function, ClassVisitor cv) {
        boolean shouldCreate = function.getParameters().stream().anyMatch(parameter -> parameter.getDefaultValue().isPresent());
        if (!shouldCreate) {
            return;
        }

        FunctionSignature defaultSignature = getDefaultSignature(function.getFunctionSignature());
        String description = DescriptorFactory.getMethodDescriptor(defaultSignature);
        String name = defaultSignature.getName();
        int mod = ModifierTransformer.transform(function.getModifiers().with(Modifier.SYNTHETIC));
        MethodVisitor mvs = cv.visitMethod(mod, name, description, null, null);
        InstructionAdapter adapter = new InstructionAdapter(mvs);

        StatementGenerator statementGenerator = statementGenerator(function, adapter);
        FunctionScope scope = statementGenerator.getScope();
        Expression owner = scope.isLocalVariableExists("this") ? new LocalVariableReference(scope.getLocalVariable("this"))
                : new EmptyExpression(scope.getClassType());
        List<Argument> functionArguments = verifyMissingArguments(function, statementGenerator);

        FunctionCall methodCall = new FunctionCall(function.getFunctionSignature(), functionArguments, owner);
        ReturnStatement returnStatement = new ReturnStatement(methodCall);
        returnStatement.accept(statementGenerator);

        adapter.visitMaxs(-1, -1);
        adapter.visitEnd();
    }


    private StatementGenerator statementGenerator(Function function, InstructionAdapter adapter) {
        Scope originalScope = ((EnkelType) function.getFunctionSignature().getOwner()).getScope();
        FunctionScope scope = new FunctionScope(originalScope, function.getFunctionSignature());
        if (!function.getModifiers().contains(Modifier.STATIC)) {
            scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        }

        FunctionGenerator functionGenerator = new FunctionGenerator(scope);
        functionGenerator.addParametersAsLocalVariables(function.getFunctionSignature());


        Parameter bitMask = new Parameter("mask", PrimitiveTypes.INT_TYPE, null);
        Parameter markerParam = new Parameter("marker", DEFAULT_MARKER, null);
        scope.addLocalVariable(new LocalVariable(bitMask.getName(), bitMask.getType()));
        scope.addLocalVariable(new LocalVariable(markerParam.getName(), markerParam.getType()));

        StatementGenerator generator = new StatementGeneratorFilter(adapter, scope);
        return new IntrinsicMethodCaller(adapter, null, generator, scope);
    }

    private List<Argument> verifyMissingArguments(Function function, StatementGenerator statementScopeGenerator) {
        FunctionScope scope = statementScopeGenerator.getScope();
        ArgumentHolder argument = new ArgumentHolder(new LocalVariableReference(scope.getLocalVariable("mask")), null);
        FunctionSignature compareSignature = PrimitiveTypes.INT_TYPE.getMethodCallSignature(ArithmeticOperator.BINAND.getMethodName(), Collections.singletonList(argument));
        for (int x = 0; x < function.getFunctionSignature().getParameters().size(); x++) {
            Parameter parameter = function.getFunctionSignature().getParameters().get(x);
            if (!parameter.getDefaultValue().isPresent()) {
                continue;
            }
            Value bitCheckValue = new Value(PrimitiveTypes.INT_TYPE, x + 1);
            FunctionCall andCompare = new FunctionCall(compareSignature, compareSignature.createArgumentList(Collections.singletonList(argument)), bitCheckValue);
            Assignment assignment = new Assignment(scope.getLocalVariable(parameter.getName()), parameter.getDefaultValue().get());
            IfStatement ifStatement = new IfStatement(andCompare, assignment);
            ifStatement.accept(statementScopeGenerator);
        }

        return function.getFunctionSignature().getParameters().stream()
                .map(parameter -> new Argument(new LocalVariableReference(scope.getLocalVariable(parameter.getName())), parameter.getName(), parameter.getType()))
                .collect(Collectors.toList());
    }

    private void createParameterLessConstructor(Constructor constructorFunction, MethodGenerator methodGenerator) {
        FunctionSignature originalFunctionSignature = constructorFunction.getFunctionSignature();
        FunctionSignature thisFunctionSignature = new FunctionSignature(originalFunctionSignature.getName(), Collections.emptyList(), originalFunctionSignature.getReturnType()
                , originalFunctionSignature.getModifiers(), originalFunctionSignature.getOwner(), SignatureType.FUNCTION_CALL);

        Scope originalScope = ((EnkelType) constructorFunction.getFunctionSignature().getOwner()).getScope();
        FunctionScope scope = new FunctionScope(originalScope, constructorFunction.getFunctionSignature());
        scope.addLocalVariable(new LocalVariable("this", scope.getClassType()));
        SuperCall superCall = new SuperCall(originalFunctionSignature);
        Block block = new Block(scope, Collections.singletonList(superCall));

        Constructor constructor = new Constructor(thisFunctionSignature, block);
        methodGenerator.generate(constructor);
    }


    private List<Parameter> getDefaultMethodParameters(FunctionSignature signature) {
        List<Parameter> parameterList = new ArrayList<>(signature.getParameters());
        Parameter bitMask = new Parameter("mask", PrimitiveTypes.INT_TYPE, null);
        Parameter markerParam = new Parameter("marker", DEFAULT_MARKER, null);
        parameterList.add(bitMask);
        parameterList.add(markerParam);
        return parameterList;
    }

}
