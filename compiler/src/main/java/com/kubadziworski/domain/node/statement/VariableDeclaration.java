package com.kubadziworski.domain.node.statement;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 28.03.16.
 */
public class VariableDeclaration extends ElementImpl implements Statement {
    private final String name;
    private final Expression expression;
    private final Type variableType;
    private final boolean mutable;

    public VariableDeclaration(NodeData context, String name, Expression expression, Type variableType, boolean mutable) {
        super(context);
        this.expression = expression;
        this.name = name;
        this.variableType = variableType;
        this.mutable = mutable;
    }

    public VariableDeclaration(String name, Expression expression, Type variableType, boolean mutable) {
        this(null, name, expression, variableType, mutable);
    }

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }

    public Type getVariableType() {
        return variableType;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
