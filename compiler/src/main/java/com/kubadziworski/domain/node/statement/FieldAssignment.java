package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.Variable;
import com.kubadziworski.exception.IncompatibleTypesException;


public class FieldAssignment extends ElementImpl implements Statement {
    private final Field field;
    private final Expression assignmentExpression;
    private final Expression preExpression;

    public FieldAssignment(Expression preExpression, Field field, Expression assignmentExpression) {
        this(null, preExpression, field, assignmentExpression);
    }

    public FieldAssignment(NodeData element, Expression preExpression, Field field, Expression assignmentExpression) {
        super(element);
        this.preExpression = preExpression;
        this.field = field;
        this.assignmentExpression = assignmentExpression;
        validateType(assignmentExpression, field);
    }

    public Field getField() {
        return field;
    }

    public Expression getAssignmentExpression() {
        return assignmentExpression;
    }

    public Expression getPreExpression() {
        return preExpression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    private static void validateType(Expression expression, Variable variable) {
        if (expression.getType().inheritsFrom(variable.getType()) < 0) {
            throw new IncompatibleTypesException(variable.getName(), variable.getType(), expression.getType());
        }
    }
}
