package com.kubadziworski.domain.statement;

import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.expression.Expression;

/**
 * Created by kuba on 23.04.16.
 */
public class AssignmentStatement implements Statement{
    private final String varName;
    private final Expression expression;

    public AssignmentStatement(String varName, Expression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    public AssignmentStatement(VariableDeclarationStatement declarationStatement) {
        this.varName = declarationStatement.getName();
        this.expression = declarationStatement.getExpression();
    }

    public String getVarName() {
        return varName;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
