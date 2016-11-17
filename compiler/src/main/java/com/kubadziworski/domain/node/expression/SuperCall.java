package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 05.05.16.
 */
public class SuperCall extends ElementImpl implements Call {
    public static final String SUPER_IDETIFIER = "super";
    private final List<Argument> arguments;

    public SuperCall() {
        this(Collections.emptyList());
    }

    public SuperCall(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public SuperCall(NodeData element, List<Argument> arguments) {
        super(element);
        this.arguments = arguments;
    }

    @Override
    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String getIdentifier() {
        return SUPER_IDETIFIER;
    }

    @Override
    public Type getType() {
        return UnitType.INSTANCE;
    }


    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
