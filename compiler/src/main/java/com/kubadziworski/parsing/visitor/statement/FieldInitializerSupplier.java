package com.kubadziworski.parsing.visitor.statement;

import com.kubadziworski.domain.scope.Field;


public interface FieldInitializerSupplier {
    FieldInitializer get(Field field);
}
