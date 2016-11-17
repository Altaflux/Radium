package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 09.04.16.
 */
public class LocalVariableReference extends ElementImpl implements Reference {

    private final LocalVariable variable;

    public LocalVariableReference(LocalVariable variable) {
        this(null, variable);
    }

    public LocalVariableReference(NodeData element, LocalVariable variable) {
        super(element);
        this.variable = variable;
    }

    @Override
    public String getName() {
        return variable.getName();
    }


    @Override
    public Type getType() {
        return variable.getType();
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }


}
