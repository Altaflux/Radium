package com.kubadziworski.domain.node.expression.function;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Opcodes;

import java.util.Collections;
import java.util.List;

public class FunctionCall extends ElementImpl implements Call, CallableMember {
    private final Expression owner;
    private final FunctionSignature signature;
    private final List<Argument> arguments;
    private final Type type;
    private final CallType callType;

    public FunctionCall(FunctionSignature signature, List<Argument> arguments, Expression owner) {
        this(null, signature, arguments, owner);
    }

    public FunctionCall(NodeData element, FunctionSignature signature, List<Argument> arguments, Expression owner) {
        this(element, signature, arguments, owner, CallType.METHOD_CALL);
    }

    public FunctionCall(FunctionSignature signature, List<Argument> arguments, Expression owner, CallType callType) {
        this(null, signature, arguments, owner, callType);
    }

    public FunctionCall(NodeData element, FunctionSignature signature, List<Argument> arguments, Expression owner, CallType callType) {
        super(element);
        this.type = signature.getReturnType();
        this.signature = signature;
        this.arguments = arguments;
        this.owner = owner;
        this.callType = callType;
    }

    @Override
    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return signature.getName();
    }

    @Override
    public String getName() {
        return signature.getName();
    }

    @Override
    public int getInvokeOpcode() {
        if (callType == CallType.SUPER_CALL) {
            return Opcodes.INVOKESPECIAL;
        }
        return signature.getInvokeOpcode();
    }

    public Type getOwnerType() {
        return signature.getOwner();
    }

    public Expression getOwner() {
        return owner;
    }

    public FunctionSignature getFunctionSignature() {
        return signature;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    @Override
    public Type getType() {
        return type;
    }
}
