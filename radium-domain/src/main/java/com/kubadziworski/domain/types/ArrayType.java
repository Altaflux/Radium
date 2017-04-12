package com.kubadziworski.domain.types;

/**
 * Created by plozano on 3/29/2017.
 */
public interface ArrayType extends ComponentType {

    ComponentType getComponentType();

    int getDimensions();
}
