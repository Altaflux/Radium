package com.kubadziworski.resolver.descriptor;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyDescriptor that = (PropertyDescriptor) o;

        if (!name.equals(that.name)) return false;
        return field.equals(that.field);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + field.hashCode();
        return result;
    }
}
