package com.kubadziworski.domain.resolver;


import com.kubadziworski.domain.type.ClassType;

public class ClassDescriptor implements DeclarationDescriptor {

    private final String name;
    private final String classPackage;

    public ClassDescriptor(String name, String classPackage) {
        this.name = name;
        this.classPackage = classPackage;
    }

    public ClassType getType() {
        return new ClassType(classPackage + "." + name);
    }

    @Override
    public String getName() {
        return name;
    }
}
