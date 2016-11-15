package com.kubadziworski.bytecodegeneration.expression;

import com.google.common.collect.Ordering;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.exception.BadArgumentsToFunctionCallException;
import com.kubadziworski.exception.WrongArgumentNameException;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Comparator;
import java.util.List;

public class CallExpressionGenerator {

    private final MethodVisitor methodVisitor;

    public CallExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ConstructorCall constructorCall, Scope scope, StatementGenerator statementGenerator) {
        FunctionSignature signature = scope.getConstructorCallSignature(constructorCall.getIdentifier(), constructorCall.getArguments());
        String ownerDescriptor = ClassTypeFactory.createClassType(signature.getName()).getInternalName();
        methodVisitor.visitTypeInsn(Opcodes.NEW, ownerDescriptor);
        methodVisitor.visitInsn(Opcodes.DUP);
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(signature);
        generateArguments(constructorCall, signature, statementGenerator);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, ownerDescriptor, "<init>", methodDescriptor, false);
    }

    public void generate(SuperCall superCall, Scope scope, StatementGenerator statementGenerator) {
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        generateArguments(superCall, scope, statementGenerator);
        String ownerDescriptor = scope.getSuperClassInternalName();
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, ownerDescriptor, "<init>", "()V" /*TODO Handle super calls with arguments*/, false);
    }

    public void generate(FunctionCall functionCall, StatementGenerator statementGenerator) {
        Expression owner = functionCall.getOwner();
        owner.accept(statementGenerator);
        generateArguments(functionCall, statementGenerator);
        String functionName = functionCall.getIdentifier();
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(functionCall.getSignature());
        String ownerDescriptor = functionCall.getOwnerType().getInternalName();
        int callOpCode = functionCall.getSignature().getInvokeOpcode();

        methodVisitor.visitMethodInsn(callOpCode, ownerDescriptor, functionName, methodDescriptor, false);
    }

    private void generateArguments(FunctionCall call, StatementGenerator statementGenerator) {
        FunctionSignature signature = call.getOwnerType()
                .getMethodCallSignature(call.getIdentifier(), call.getArguments());
        generateArguments(call, signature, statementGenerator);
    }

    private void generateArguments(SuperCall call, Scope scope, StatementGenerator statementGenerator) {
        FunctionSignature signature = scope.getMethodCallSignature(call.getIdentifier(), call.getArguments());
        generateArguments(call, signature, statementGenerator);
    }

    private void generateArguments(ConstructorCall call, Scope scope, StatementGenerator statementGenerator) {
        FunctionSignature signature = scope.getConstructorCallSignature(call.getIdentifier(), call.getArguments());
        generateArguments(call, signature, statementGenerator);
    }


    private void generateArguments(Call call, FunctionSignature signature, StatementGenerator statementGenerator) {
        List<Parameter> parameters = signature.getParameters();
        List<Argument> arguments = call.getArguments();
        if (arguments.size() > parameters.size()) {
            throw new BadArgumentsToFunctionCallException(call);
        }
        arguments = getSortedArguments(arguments, parameters);
        arguments.forEach(argument -> argument.accept(statementGenerator));
        generateDefaultParameters(call, parameters, arguments, statementGenerator);
    }

    private List<Argument> getSortedArguments(List<Argument> arguments, List<Parameter> parameters) {
        Comparator<Argument> argumentIndexComparator = (o1, o2) -> {
            if (!o1.getParameterName().isPresent()) return 0;
            return getIndexOfArgument(o1, parameters) - getIndexOfArgument(o2, parameters);
        };
        return Ordering.from(argumentIndexComparator).immutableSortedCopy(arguments);
    }

    private Integer getIndexOfArgument(Argument argument, List<Parameter> parameters) {
        String paramName = argument.getParameterName().get();
        return parameters.stream()
                .filter(p -> p.getName().equals(paramName))
                .map(parameters::indexOf)
                .findFirst()
                .orElseThrow(() -> new WrongArgumentNameException(argument, parameters));
    }

    private void generateDefaultParameters(Call call, List<Parameter> parameters, List<Argument> arguments, StatementGenerator statementGenerator) {
        for (int i = arguments.size(); i < parameters.size(); i++) {
            Expression defaultParameter = parameters.get(i).getDefaultValue()
                    .orElseThrow(() -> new BadArgumentsToFunctionCallException(call));
            defaultParameter.accept(statementGenerator);
        }
    }
}