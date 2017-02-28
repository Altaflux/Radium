package com.kubadziworski.parsing.visitor.statement;


import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;

public interface FieldInitializer {
    Statement supply(Scope scope);
}
