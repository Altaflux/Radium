package com.kubadziworski.domain.classs;

import com.kubadziworski.bytecodegenerator.ExpressionGenrator;
import com.kubadziworski.domain.expression.FunctionParameter;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.statement.Statement;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class Function {

    private final String name;
    private final List<FunctionParameter> arguments;
    private final List<Statement> statements;
    private final Type returnType;
    private Scope scope;

    public Function(Scope scope, String name, Type returnType, List<FunctionParameter> arguments, List<Statement> statements) {
        this.scope = scope;
        this.name = name;
        this.arguments = arguments;
        this.statements = statements;
        this.returnType = returnType;
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

    public Type getReturnType() {
        return returnType;
    }
}
