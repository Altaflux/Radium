package com.kubadziworski.domain.type;


public class DefaultTypes {

    private DefaultTypes() {
    }

    public static JavaClassType STRING = new JavaClassType("java.lang.String");

    public static JavaClassType Integer() {
        return new JavaClassType("java.lang.Integer");
    }

    public static JavaClassType Double() {
        return new JavaClassType("java.lang.Double");
    }

    public static JavaClassType Boolean() {
        return new JavaClassType("java.lang.Boolean");
    }

    public static JavaClassType Float() {
        return new JavaClassType("java.lang.Float");
    }

    public static Type String() {
        return new JavaClassType("java.lang.String");
    }
}
