package com.kubadziworski.domain;


import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.type.ClassType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class ClassDeclaration {

    private final String name;
    private final List<Field> fields;
    private final List<Function> methods;
    private final ClassType classType;

    public ClassDeclaration(String name,ClassType classType, List<Field> fields, List<Function> methods) {
        this.name = name;
        this.fields = fields;
        this.methods = methods;
        this.classType = classType;
    }

    public String getName() {
        return name;
    }

    public ClassType getClassType() {
        return classType;
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public List<Function> getMethods() {
        return Collections.unmodifiableList(methods);
    }

}
