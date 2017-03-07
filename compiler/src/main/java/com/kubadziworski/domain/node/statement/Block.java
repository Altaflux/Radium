package com.kubadziworski.domain.node.statement;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.ElementImpl;
import com.kubadziworski.domain.node.NodeData;
import com.kubadziworski.domain.scope.FunctionScope;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 13.04.16.
 */
public class Block extends ElementImpl implements Statement {
    private final List<Statement> statements;
    private final FunctionScope scope;

    public Block(NodeData element, FunctionScope scope, List<Statement> statements) {
        super(element);
        this.scope = scope;
        this.statements = statements;
    }

    public Block(FunctionScope scope, List<Statement> statements) {
        this(null, scope, statements);
    }

    public static Block empty(FunctionScope scope) {
        return new Block(scope, Collections.emptyList());
    }

    @Override
    public boolean isReturnComplete() {
        if (!statements.isEmpty()) {
            Statement lastStatement = statements.get(statements.size() - 1);
            return lastStatement.isReturnComplete();
        }
        return false;
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }

    public FunctionScope getScope() {
        return scope;
    }

    public List<Statement> getStatements() {
        return Collections.unmodifiableList(statements);
    }


}
