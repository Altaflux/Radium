package com.kubadziworski.antlr.domain.classs;

import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.expression.FunctionParameter;
import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.antlr.domain.statement.Statement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class Function extends Expression {

    private final String name;
    private final List<FunctionParameter> arguments;
    private final List<Statement> statements;
    private Scope scope;

    public Function(Scope scope, String name, Type returnType, List<FunctionParameter> arguments, List<Statement> statements) {
        super(returnType);
        this.scope = scope;
        this.name = name;
        this.arguments = arguments;
        this.statements = statements;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public Collection<Statement> getStatements() {
        return Collections.unmodifiableCollection(statements);
    }

    public Scope getScope() {
        return scope;
    }
}
