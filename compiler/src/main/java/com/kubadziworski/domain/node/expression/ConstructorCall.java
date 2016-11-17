package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import lombok.ToString;

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

    public ConstructorCall(String identifier) {
        this(null, identifier, Collections.emptyList());
    }

    public ConstructorCall(NodeData element, String identifier) {
        this(element, identifier, Collections.emptyList());
    }

    public ConstructorCall(String className, List<Argument> arguments) {
        this(null, className, arguments);
    }

    public ConstructorCall(NodeData element, String className, List<Argument> arguments) {
        super(element);
        this.type = ClassTypeFactory.createClassType(className);
        this.arguments = arguments;
        this.identifier = type.getName();
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
    public Type getType() {
        return type;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
