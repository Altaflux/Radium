package com.kubadziworski.bytecodegeneration.expression;

import com.google.common.collect.Ordering;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.expression.function.ConstructorCall;
import com.kubadziworski.domain.node.expression.function.FunctionCall;
import com.kubadziworski.domain.node.expression.function.SuperCall;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.exception.WrongArgumentNameException;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.Comparator;
import java.util.List;

public class CallExpressionGenerator {

    private final InstructionAdapter methodVisitor;

    CallExpressionGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(SuperCall superCall, FunctionScope scope, StatementGenerator statementGenerator) {
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        generateArguments(superCall.getArguments(), superCall.getFunctionSignature().getParameters(), statementGenerator);
        String ownerDescriptor = scope.getSuperClassType().getAsmType().getInternalName();
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(superCall.getFunctionSignature());
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, ownerDescriptor, "<init>", methodDescriptor, false);
    }


    public void generate(ConstructorCall constructorCall, FunctionScope scope, StatementGenerator statementGenerator) {
        FunctionSignature signature = constructorCall.getFunctionSignature();
        String ownerDescriptor = signature.getOwner().getAsmType().getInternalName();
        methodVisitor.visitTypeInsn(Opcodes.NEW, ownerDescriptor);
        methodVisitor.visitInsn(Opcodes.DUP);

        generateArguments(constructorCall.getArguments(), constructorCall.getFunctionSignature().getParameters(), statementGenerator);
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(signature);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, ownerDescriptor, "<init>", methodDescriptor, false);
    }

    public void generate(FunctionCall functionCall, FunctionScope scope, StatementGenerator statementGenerator) {
        Expression owner = functionCall.getOwner();
        owner.accept(statementGenerator);
        generateArguments(functionCall.getArguments(), functionCall.getFunctionSignature().getParameters(), statementGenerator);
        String functionName = functionCall.getIdentifier();
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(functionCall.getFunctionSignature());
        String ownerDescriptor = functionCall.getOwnerType().getAsmType().getInternalName();
        int callOpCode = functionCall.getInvokeOpcode();
        methodVisitor.visitMethodInsn(callOpCode, ownerDescriptor, functionName, methodDescriptor, false);
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


    private void generateArguments(List<Argument> arguments, List<Parameter> parameters, StatementGenerator statementGenerator) {
        getSortedArguments(arguments, parameters).forEach(argument -> argument.accept(statementGenerator));
    }
}
