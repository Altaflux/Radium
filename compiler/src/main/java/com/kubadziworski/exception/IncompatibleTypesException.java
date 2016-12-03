package com.kubadziworski.exception;

import com.kubadziworski.domain.type.Type;


public class IncompatibleTypesException extends RuntimeException {

    public IncompatibleTypesException(String name, Type type1, Type type2) {
        super("Type incompatibility for declaration: " + name + " of type: " + type1.getName() + " and expression of type: " + type2.getName());

    }
}
