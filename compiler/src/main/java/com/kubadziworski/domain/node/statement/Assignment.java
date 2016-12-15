package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.Variable;

public class Assignment extends ElementImpl implements Statement {
    private final Variable variable;
    private final Expression assignmentExpression;

    public Assignment(Variable variable, Expression assignmentExpression) {
        this(null, variable, assignmentExpression);
    }

    public Assignment(NodeData element, Variable variable, Expression assignmentExpression) {
        super(element);
        this.variable = variable;
        this.assignmentExpression = assignmentExpression;
    }

    public Assignment(VariableDeclaration declarationStatement) {
        this.variable = declarationStatement.getVariable();
        this.assignmentExpression = declarationStatement.getExpression();
    }

    public Variable getVariable() {
        return variable;
    }

    public Expression getAssignmentExpression() {
        return assignmentExpression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
