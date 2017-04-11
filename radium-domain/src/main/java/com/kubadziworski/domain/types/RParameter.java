package com.kubadziworski.domain.types;

/**
 * Created by plozano on 4/10/2017.
 */
public class RParameter implements RExpression {
    private final String name;
    private final RExpression defaultValue;
    private final TypeReference type;
    private final boolean visible;

    public RParameter(String name, TypeReference type, RExpression defaultValue) {
        this(name, type, defaultValue, true);
    }

    public RParameter(String name, TypeReference type, RExpression defaultValue, boolean visible) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
        this.visible = visible;
        if (defaultValue != null) {
            //   validateType(defaultValue, type);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public TypeReference getType() {
        return type;
    }

    public boolean isVisible() {
        return visible;
    }

}
