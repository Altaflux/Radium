package com.kubadziworski.domain.scope;

import com.kubadziworski.bytecodegeneration.FieldGenerator;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * Created by kuba on 13.05.16.
 */
public class Field implements Variable {

    private final String name;
    private final Type owner;
    private final Type type;
    private final int modifiers;
    private final Expression initialExpression;

    private Function getterFunction;
    private Function setterFunction;

    public Field(String name, Type owner, Type type, int modifiers) {
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.modifiers = modifiers;
        initialExpression = null;
    }

    public Field(String name, Type owner, Type type, Expression initialExpression, int modifiers) {
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.modifiers = modifiers;
        this.initialExpression = initialExpression;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getModifiers() {
        return modifiers;
    }

    public Type getOwner() {
        return owner;
    }

    public String getOwnerInternalName() {
        return owner.getInternalName();
    }

    public int getInvokeOpcode() {
        if (Modifier.isStatic(modifiers)) {
            return Opcodes.GETSTATIC;
        }
        return Opcodes.GETFIELD;
    }

    public void accept(FieldGenerator generator) {
        generator.generate(this);
    }

    public Function getGetterFunction() {
        return getterFunction;
    }

    public void setGetterFunction(Function getterFunction) {
        this.getterFunction = getterFunction;
    }

    public Function getSetterFunction() {
        return setterFunction;
    }

    public void setSetterFunction(Function setterFunction) {
        this.setterFunction = setterFunction;
    }

    public Optional<Expression> getInitialExpression() {
        return Optional.ofNullable(initialExpression);
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
