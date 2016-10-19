package com.kubadziworski.domain.node.expression;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Modifier;


public class StaticFieldReference implements Reference {

    private final Field field;

    public StaticFieldReference(Field field) {
        Validate.isTrue(Modifier.isStatic(field.getModifiers()), "The field '%s' is not static", field.getName());
        this.field = field;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Type getType() {
        return field.getType();
    }

    public String getOwnerInternalName(){
        return field.getOwnerInternalName();
    }

    @Override
    public void accept(ExpressionGenerator generator) {
        generator.generate(this);
    }

    @Override
    public void accept(StatementGenerator generator) {
        generator.generate(this);
    }
}
