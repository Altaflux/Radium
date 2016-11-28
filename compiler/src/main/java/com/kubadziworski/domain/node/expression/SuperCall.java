package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 05.05.16.
 */
public class SuperCall extends ElementImpl implements Call {

    public static final String SUPER_IDENTIFIER = "super";

    private final List<Argument> arguments;
    private final FunctionSignature functionSignature;

    public SuperCall(FunctionSignature functionSignature) {
        this(functionSignature, Collections.emptyList());
    }

    public SuperCall(FunctionSignature functionSignature, List<Argument> arguments) {
        this(null, functionSignature, arguments);
    }

    public SuperCall(NodeData element,FunctionSignature functionSignature, List<Argument> arguments) {
        super(element);
        this.arguments = arguments;
        this.functionSignature = functionSignature;
    }

    @Override
    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return SUPER_IDENTIFIER;
    }

    @Override
    public int getInvokeOpcode() {
        return Opcodes.INVOKESPECIAL;
    }

    @Override
    public Type getType() {
        return UnitType.INSTANCE;
    }

    public FunctionSignature getFunctionSignature() {
        return functionSignature;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
