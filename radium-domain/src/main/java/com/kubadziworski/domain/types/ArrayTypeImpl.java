package com.kubadziworski.domain.types;

/**
 * Created by pablo.lozano on 4/17/2017.
 */
public class ArrayTypeImpl extends ComponentTypeImpl implements ArrayType {

    private final ComponentType componentType;

    public ArrayTypeImpl(ComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public String getQualifiedName() {
        ComponentType componentType = getComponentType();
        if (componentType != null)
            return componentType.getSimpleName() + "[]";
        return null;
    }

    @Override
    public String getSimpleName() {
        ComponentType componentType = getComponentType();
        if (componentType != null)
            return componentType.getSimpleName() + "[]";
        return null;
    }

    @Override
    public ComponentType getComponentType() {
        return componentType;
    }

    @Override
    public int getDimensions() {
        int result = 1;
        ComponentType componentType = getComponentType();
        if (componentType == null)
            throw new NullPointerException("component type may not be null");
        while (componentType.unwrap() instanceof ArrayType) {
            result++;
            componentType = ((ArrayType) componentType).getComponentType();
            if (componentType == null) {
                return result;
            }
        }
        return result;
    }
}
