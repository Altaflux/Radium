package com.kubadziworski.domain.node.expression.function;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import lombok.ToString;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 05.05.16.
 */
@ToString
public class ConstructorCall extends ElementImpl implements Call {
    private final List<Argument> arguments;
    private final Type type;
    private final String identifier;
    private final FunctionSignature functionSignature;

    public ConstructorCall(FunctionSignature functionSignature, Type type) {
        this(null, functionSignature, type, Collections.emptyList());
    }

    public ConstructorCall(NodeData element, FunctionSignature functionSignature, Type type) {
        this(element, functionSignature, type, Collections.emptyList());
    }

    public ConstructorCall(FunctionSignature functionSignature, Type type, List<Argument> arguments) {
        this(null, functionSignature, type, arguments);
    }

    public ConstructorCall(NodeData element, FunctionSignature functionSignature, Type type, List<Argument> arguments) {
        super(element);
        this.type = type;
        this.arguments = arguments;
        this.identifier = type.getName();
        this.functionSignature = functionSignature;
    }

    public FunctionSignature getFunctionSignature() {
        return functionSignature;
    }

    @Override
    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public int getInvokeOpcode() {
        return Opcodes.INVOKESPECIAL;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
