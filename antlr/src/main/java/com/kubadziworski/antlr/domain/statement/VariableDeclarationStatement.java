package com.kubadziworski.antlr.domain.statement;


import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.expression.Identifier;

/**
 * Created by kuba on 28.03.16.
 */
public class VariableDeclarationStatement implements Statement {
    private final String name;
    private final Expression expression;
    private final int index;

    public VariableDeclarationStatement(String name, Expression expression, int index) {
        this.expression = expression;
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public Expression getExpression() {
        return expression;
    }
}
