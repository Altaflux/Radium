package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;

import java.util.Optional;

/**
 * Created by kuba on 23.04.16.
 */
public class Assignment implements Statement {
    private final String varName;
    private final Expression assignmentExpression;
    private final Expression preExpression;

    public Assignment(Expression preExpression, String varName, Expression assignmentExpression) {
        this.preExpression = preExpression;
        this.varName = varName;
        this.assignmentExpression = assignmentExpression;
    }

    public Assignment(String varName, Expression assignmentExpression) {
        this.preExpression = null;
        this.varName = varName;
        this.assignmentExpression = assignmentExpression;
    }

    public Assignment(VariableDeclaration declarationStatement) {
        this.preExpression = null;
        this.varName = declarationStatement.getName();
        this.assignmentExpression = declarationStatement.getExpression();
    }

    public String getVarName() {
        return varName;
    }

    public Expression getAssignmentExpression() {
        return assignmentExpression;
    }

    public Optional<Expression> getPreExpression() {
        return Optional.ofNullable(preExpression);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
