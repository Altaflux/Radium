package com.kubadziworski.domain.scope;

import com.kubadziworski.bytecodegeneration.FieldGenerator;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.parsing.visitor.statement.FieldInitializer;
import com.kubadziworski.parsing.visitor.statement.FieldInitializerSupplier;
import lombok.Builder;

import java.util.Optional;


@Builder(toBuilder = true)
public class Field implements Variable, CallableDescriptor {

    private final String name;
    private final Type owner;
    private final Type type;
    private final Modifiers modifiers;
    private final FieldInitializerSupplier initialExpression;

    private final FieldAccessorSupplier getterFunction;
    private final FieldAccessorSupplier setterFunction;

    public Field(String name, Type owner, Type type, Modifiers modifiers, FieldInitializerSupplier initialExpression,
                 FieldAccessorSupplier getterFunction, FieldAccessorSupplier setterFunction) {
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.modifiers = modifiers;
        this.initialExpression = initialExpression;
        this.getterFunction = getterFunction;
        this.setterFunction = setterFunction;
    }


    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public Type getOwner() {
        return owner;
    }

    public void accept(FieldGenerator generator) {
        generator.generate(this);
    }


    public Optional<Function> getGetterFunction() {
        return Optional.ofNullable(getterFunction)
                .map(fieldInitializerSupplier -> fieldInitializerSupplier.get(this));
    }

    public Optional<Function> getSetterFunction() {
        return Optional.ofNullable(setterFunction)
                .map(fieldInitializerSupplier -> fieldInitializerSupplier.get(this));
    }

    public Optional<FieldInitializer> getInitialExpression() {
        return Optional.ofNullable(initialExpression)
                .map(fieldInitializerSupplier -> fieldInitializerSupplier.get(this));
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", type=" + type +
                ", modifiers=" + modifiers +
                '}';
    }
}
