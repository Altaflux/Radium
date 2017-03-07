package com.kubadziworski.parsing.visitor.statement;


import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.FunctionScope;

public interface FieldInitializer {
    Statement supply(FunctionScope scope);
}
