package com.kubadziworski.domain.node.expression;

import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Opcodes;

import java.util.List;


public class SuperFunctionCall extends FunctionCall {

    public SuperFunctionCall(FunctionSignature signature, List<Argument> arguments, Expression owner) {
        this(null, signature, arguments, owner);
    }

    public SuperFunctionCall(NodeData element, FunctionSignature signature, List<Argument> arguments, Expression owner) {
        super(element, signature, arguments, owner);
    }

    @Override
    public int getInvokeOpcode() {
        return Opcodes.INVOKESPECIAL;
    }


    public Type getOwnerType() {
        return getFunctionSignature().getOwner();
    }

}
