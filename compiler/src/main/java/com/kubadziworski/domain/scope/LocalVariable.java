package com.kubadziworski.domain.scope;

import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 09.04.16.
 */
public class LocalVariable implements Variable {
    private final String name;
    private final boolean mutable;
    private Type type;

    public LocalVariable(String name, Type type) {
        this(name, type, true);
    }

    public LocalVariable(String name, Type type, boolean mutable) {
        this.type = type;
        this.name = name;
        this.mutable = mutable;
    }

    public boolean isMutable() {
        return mutable;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    public void changeType(Type type) {
        this.type = type;
    }
}
