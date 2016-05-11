package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.scope.Scope;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 13.04.16.
 */
public class Block implements Statement {
    private final List<Statement> statements;
    private final Scope scope;

    public Block(Scope scope, List<Statement> statements) {
        this.scope = scope;
        this.statements = statements;
    }

    public static Block empty(Scope scope) {
        return new Block(scope, Collections.emptyList());
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public Scope getScope() {
        return scope;
    }

    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }
}
