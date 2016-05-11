package com.kubadziworski.domain;


import com.kubadziworski.domain.Function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class ClassDeclaration {

    private final String name;
    private final Collection<Function> methods;

    public ClassDeclaration(String name, Collection<Function> methods) {
        this.name = name;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public List<Function> getMethods() {
        return new ArrayList<>(methods);
    }

}
