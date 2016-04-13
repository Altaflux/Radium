package com.kubadziworski.domain.classs;

import com.kubadziworski.domain.expression.FunctionParameter;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.statement.Statement;

import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class Function {

    private final String name;
    private final List<FunctionParameter> arguments;
    private final Statement rootStatement;
    private final Type returnType;

    public Function(String name, Type returnType, List<FunctionParameter> arguments, Statement rootStatement) {
        this.name = name;
        this.arguments = arguments;
        this.rootStatement = rootStatement;
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<FunctionParameter> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public Statement getRootStatement() {
        return rootStatement;
    }

    public Type getReturnType() {
        return returnType;
    }
}
