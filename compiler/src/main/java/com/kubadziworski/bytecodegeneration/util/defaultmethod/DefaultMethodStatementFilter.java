package com.kubadziworski.bytecodegeneration.util.defaultmethod;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class DefaultMethodStatementFilter extends StatementGeneratorFilter {

    private final InstructionAdapter adapter;
    private final DefaultMethodHandler methodHandler = new DefaultMethodHandler();
    private static Type DEFAULT_MARKER = ClassTypeFactory.createClassType("radium.internal.DefaultConstructorMarker");

    public DefaultMethodStatementFilter(InstructionAdapter adapter, StatementGenerator parent, StatementGenerator next, Scope scope) {
        super(parent, next, scope);
        this.adapter = adapter;
    }

    public void generate(SuperCall superCall, StatementGenerator statementGenerator) {
        FunctionSignature originalSignature = superCall.getFunctionSignature();
        List<Argument> fusedArguments = constructArguments(superCall.getArguments(), originalSignature.getParameters());
        boolean callDefault = fusedArguments.stream().anyMatch(Argument::isDefaultValue);
        if (!callDefault) {
            next.generate(superCall, statementGenerator);
            return;
        }

        FunctionSignature defaultFunctionSignature = methodHandler.getDefaultSignatureForConstructor(originalSignature);
        fusedArguments.addAll(generateMaskArguments(fusedArguments));
        SuperCall newCall = new SuperCall(defaultFunctionSignature, fusedArguments);
        next.generate(newCall, statementGenerator);
    }

    public void generate(ConstructorCall constructorCall, StatementGenerator statementGenerator) {
        FunctionSignature originalSignature = constructorCall.getFunctionSignature();
        List<Argument> fusedArguments = constructArguments(constructorCall.getArguments(), originalSignature.getParameters());
        boolean callDefault = fusedArguments.stream().anyMatch(Argument::isDefaultValue);

        if (!callDefault) {
            next.generate(constructorCall, statementGenerator);
            return;
        }

        FunctionSignature defaultFunctionSignature = methodHandler.getDefaultSignatureForConstructor(originalSignature);
        fusedArguments.addAll(generateMaskArguments(fusedArguments));
        ConstructorCall newCall = new ConstructorCall(defaultFunctionSignature, constructorCall.getType(), fusedArguments);
        next.generate(newCall, statementGenerator);
    }

    public void generate(FunctionCall functionCall, StatementGenerator statementGenerator) {
        FunctionSignature originalSignature = functionCall.getFunctionSignature();
        List<Argument> fusedArguments = constructArguments(functionCall.getArguments(), originalSignature.getParameters());

        boolean callDefault = fusedArguments.stream().anyMatch(Argument::isDefaultValue);
        if (!callDefault) {
            next.generate(functionCall, statementGenerator);
            return;
        }

        FunctionSignature defaultFunctionSignature = methodHandler.getDefaultSignature(originalSignature);
        fusedArguments.addAll(generateMaskArguments(fusedArguments));
        FunctionCall newCall = new FunctionCall(defaultFunctionSignature, fusedArguments, functionCall.getOwner());
        next.generate(newCall, statementGenerator);
    }


    private List<Argument> constructArguments(List<Argument> arguments, List<Parameter> parameters) {
        if (arguments.size() == parameters.size()) {
            return arguments;
        }

        return IntStream.range(0, parameters.size()).mapToObj(value -> {
            Parameter parameter = parameters.get(value);
            boolean argumentByName = arguments.stream().filter(argument -> argument.getParameterName().isPresent())
                    .anyMatch(argument -> argument.getParameterName().get().equals(parameter.getName()));

            if (!argumentByName) {
                if (arguments.size() > value && !arguments.get(value).getParameterName().isPresent()) {
                    Argument def = arguments.get(value);
                    return new Argument(def.getExpression(), parameter.getName(), def.getReceiverType());
                } else {
                    if (PrimitiveTypesWrapperFactory.isPrimitiveType(parameter.getType())) {
                        Value defaultValue = PrimitiveTypesWrapperFactory.primitiveDummyValue(parameter.getType());
                        return new Argument(defaultValue, parameter.getName(), parameter.getType(), true);
                    }
                    return new Argument(new Value(NullType.INSTANCE, null), parameter.getName(), parameter.getType(), true);
                }
            } else {
                return arguments.stream().filter(argument -> argument.getParameterName().isPresent())
                        .filter(argument -> argument.getParameterName().get().equals(parameter.getName()))
                        .findAny().get();
            }
        }).collect(Collectors.toList());
    }


    private List<Argument> generateMaskArguments(List<Argument> arguments) {
        int mask = 0;
        for (int x = 0; x < arguments.size(); x++) {
            mask = mask | ((arguments.get(x).isDefaultValue() ? 1 : 0) << x);
        }
        List<Argument> newArgumentList = new ArrayList<>();
        newArgumentList.add(new Argument(new Value(PrimitiveTypes.INT_TYPE, mask), "mask", PrimitiveTypes.INT_TYPE));
        newArgumentList.add(new Argument(new Value(NullType.INSTANCE, null), "marker", DEFAULT_MARKER));

        return newArgumentList;
    }

    public StatementGenerator copy(StatementGenerator parent) {
        return new DefaultMethodStatementFilter(adapter, parent, this.next, getScope());
    }
}
