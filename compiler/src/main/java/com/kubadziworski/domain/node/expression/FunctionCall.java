package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 02.04.16.
 */
public class FunctionCall extends ElementImpl implements Call {
    private final Expression owner;
    private final FunctionSignature signature;
    private final List<Argument> arguments;
    private final Type type;

    public FunctionCall(FunctionSignature signature, List<Argument> arguments, Expression owner) {
        this(null, signature, arguments, owner);
    }

    public FunctionCall(FunctionSignature signature, List<Argument> arguments, Type ownerType) {
        this(null, signature,arguments, new EmptyExpression(ownerType));
    }

    public FunctionCall(NodeData element, FunctionSignature signature, List<Argument> arguments, Type ownerType) {
        this(element, signature,arguments, new EmptyExpression(ownerType));
    }

    public FunctionCall(NodeData element, FunctionSignature signature, List<Argument> arguments, Expression owner) {
        super(element);
        this.type = signature.getReturnType();
        this.signature = signature;
        this.arguments = arguments;
        this.owner = owner;
    }

    @Override
    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return signature.getName();
    }

    public Type getOwnerType() {
        return owner.getType();
    }

    public Expression getOwner() {
        return owner;
    }

    public FunctionSignature getSignature() {
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
