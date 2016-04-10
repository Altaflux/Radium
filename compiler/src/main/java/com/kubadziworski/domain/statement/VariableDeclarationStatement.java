package com.kubadziworski.domain.statement;


import com.kubadziworski.bytecodegenerator.StatementGenerator;
import com.kubadziworski.domain.expression.Expression;

/**
 * Created by kuba on 28.03.16.
 */
public class VariableDeclarationStatement implements Statement {
    private final String name;
    private final Expression expression;

    public VariableDeclarationStatement(String name, Expression expression) {
        this.expression = expression;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
