package com.kubadziworski.bytecodegeneration.expression;

import com.google.common.collect.Ordering;
import com.kubadziworski.bytecodegeneration.intrinsics.IntrinsicMethods;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import com.kubadziworski.exception.BadArgumentsToFunctionCallException;
import com.kubadziworski.exception.WrongArgumentNameException;
import com.kubadziworski.util.DescriptorFactory;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CallExpressionGenerator {

    private final InstructionAdapter methodVisitor;

    private static ThreadLocal<IntrinsicMethods> intrinsicMethods = ThreadLocal.withInitial(IntrinsicMethods::new);

    public CallExpressionGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ConstructorCall constructorCall, Scope scope, StatementGenerator statementGenerator) {
        FunctionSignature signature = constructorCall.getFunctionSignature();
        String ownerDescriptor = signature.getOwner().getAsmType().getInternalName();
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

    public void generate(FunctionCall functionCall, Scope scope, StatementGenerator statementGenerator) {
        Expression owner = functionCall.getOwner();


        Optional<Expression> intrinsicExpression = callArithmeticExpression(functionCall, statementGenerator);
        if (intrinsicExpression.isPresent()) {
            intrinsicExpression.get().accept(statementGenerator);
            return;
        }


        owner.accept(statementGenerator);
        generateArguments(functionCall, statementGenerator);
        String functionName = functionCall.getIdentifier();
        String methodDescriptor = DescriptorFactory.getMethodDescriptor(functionCall.getSignature());
        String ownerDescriptor = functionCall.getOwnerType().getAsmType().getInternalName();
        int callOpCode = functionCall.getInvokeOpcode();

        methodVisitor.visitMethodInsn(callOpCode, ownerDescriptor, functionName, methodDescriptor, false);
    }

    private void generateArguments(FunctionCall call, StatementGenerator statementGenerator) {
        FunctionSignature signature = call.getSignature();
        generateArguments(call, signature, statementGenerator);
    }

    private void generateArguments(SuperCall call, Scope scope, StatementGenerator statementGenerator) {
        generateArguments(call, call.getFunctionSignature(), statementGenerator);
    }

    private void generateArguments(Call call, FunctionSignature signature, StatementGenerator statementGenerator) {
        List<Parameter> parameters = signature.getParameters();
        List<Argument> arguments = call.getArguments();
        if (arguments.size() > parameters.size()) {
            throw new BadArgumentsToFunctionCallException(call);
        }
        arguments = fuseDefaults(arguments, parameters);
        arguments.forEach(argument -> argument.accept(statementGenerator));
    }


    private List<Argument> fuseDefaults(List<Argument> arguments, List<Parameter> parameters) {
        if (arguments.size() == parameters.size()) {
            return getSortedArguments(arguments, parameters);
        }

        List<Argument> fusedArguments = IntStream.range(0, parameters.size()).mapToObj(value -> {
            Parameter parameter = parameters.get(value);
            boolean argByName = arguments.stream().filter(argument -> argument.getParameterName().isPresent())
                    .anyMatch(argument -> argument.getParameterName().get().equals(parameter.getName()));
            if (!argByName) {
                if (arguments.size() > value && !arguments.get(value).getParameterName().isPresent()) {
                    Argument def = arguments.get(value);
                    return new Argument(def.getExpression(), parameter.getName(), def.getReceiverType());
                } else {
                    return new Argument(parameter.getDefaultValue().get(), parameter.getName(), parameter.getType());
                }
            } else {
                return arguments.stream().filter(argument -> argument.getParameterName().isPresent())
                        .filter(argument -> argument.getParameterName().get().equals(parameter.getName()))
                        .findAny().get();
            }
        }).collect(Collectors.toList());

        return getSortedArguments(fusedArguments, parameters);
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


    private Optional<Expression> callArithmeticExpression(FunctionCall functionCall, StatementGenerator statementGenerator) {
        return intrinsicMethods.get().intrinsicMethod(functionCall).map(intrinsicMethod -> intrinsicMethod.toExpression(functionCall, methodVisitor));

    }


    private void generateDefaultParameters2(List<Parameter> parameters, List<Argument> arguments, StatementGenerator statementGenerator) {
        for (int i = arguments.size(); i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);
            if (PrimitiveTypesWrapperFactory.isPrimitiveType(parameter.getType())) {
                Value value = ((AbstractPrimitiveType) parameter.getType()).primitiveDummyValue();
                value.accept(statementGenerator);
            } else {
                new Value(NullType.INSTANCE, null).accept(statementGenerator);
            }
        }
    }

}
