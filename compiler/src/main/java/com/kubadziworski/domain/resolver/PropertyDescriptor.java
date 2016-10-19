package com.kubadziworski.domain.resolver;

import com.kubadziworski.domain.scope.Field;

public class PropertyDescriptor implements DeclarationDescriptor {

    private final String name;
    private final Field field;

    public PropertyDescriptor(String name, Field field) {
        this.name = name;
        this.field = field;
    }

    @Override
    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }
}
