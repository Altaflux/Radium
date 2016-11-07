package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.VariableDeclaration;

public class VariableDeclarationStatementGenerator {

    public void generate(VariableDeclaration variableDeclaration, StatementGenerator statementGenerator) {
        Expression expression = variableDeclaration.getExpression();
        expression.accept(statementGenerator);
        Assignment assignment = new Assignment(variableDeclaration);
        assignment.accept(statementGenerator);

    }
}