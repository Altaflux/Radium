package com.kubadziworski.domain.resolver;


import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.lang3.StringUtils;

public class ClassDescriptor implements DeclarationDescriptor {

    private final String name;
    private final String classPackage;

    public ClassDescriptor(String name, String classPackage) {
        this.name = name;
        this.classPackage = classPackage;
    }

    public Type getType() {
        return ClassTypeFactory.createClassType(getFullClassName());
    }

    public String getFullClassName() {
        if (StringUtils.isNotEmpty(classPackage)) {
            return classPackage + "." + name;
        }
        return name;
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

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }


    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        ClassDescriptor that = (ClassDescriptor) o;
//
//        if (!name.equals(that.name)) return false;
//        return classPackage.equals(that.classPackage);
//
//    }

//    @Override
//    public int hashCode() {
//        int result = name.hashCode();
//        result = 31 * result + classPackage.hashCode();
//        return result;
//    }

    @Override
    public String toString() {
        return "ClassDescriptor{" +
                "name='" + name + '\'' +
                ", classPackage='" + classPackage + '\'' +
                '}';
    }
}
