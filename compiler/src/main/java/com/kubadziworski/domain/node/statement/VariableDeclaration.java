package com.kubadziworski.domain.node.statement;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.LocalVariable;

public class VariableDeclaration extends ElementImpl implements Statement {

    private final LocalVariable variable;
    private final Expression expression;

    public VariableDeclaration(NodeData context, LocalVariable variable, Expression expression) {
        super(context);
        this.expression = expression;
        this.variable = variable;
    }

    public VariableDeclaration(LocalVariable variable, Expression expression) {
        this(null, variable, expression);
    }

    public LocalVariable getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
