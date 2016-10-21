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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassDescriptor that = (ClassDescriptor) o;

        if (!name.equals(that.name)) return false;
        return classPackage.equals(that.classPackage);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + classPackage.hashCode();
        return result;
    }
}
