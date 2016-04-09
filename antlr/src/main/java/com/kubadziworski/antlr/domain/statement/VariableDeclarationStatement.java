package com.kubadziworski.antlr.domain.statement;


import com.kubadziworski.antlr.domain.expression.Expression;

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
}
