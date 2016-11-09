package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.VariableDeclaration;

public class VariableDeclarationStatementGenerator {

    public void generate(VariableDeclaration variableDeclaration, StatementGenerator statementGenerator) {
        Assignment assignment = new Assignment(variableDeclaration, true);
        assignment.accept(statementGenerator);
    }
}