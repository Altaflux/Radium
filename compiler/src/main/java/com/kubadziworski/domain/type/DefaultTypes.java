package com.kubadziworski.domain.type;


public class DefaultTypes {

    private DefaultTypes() {
    }

    public static JavaClassType STRING = new JavaClassType(java.lang.String.class);

    public static JavaClassType Integer() {
        return new JavaClassType(java.lang.Integer.class);
    }

    public static JavaClassType Double() {
        return new JavaClassType(java.lang.Double.class);
    }

    public static JavaClassType Boolean() {
        return new JavaClassType(java.lang.Boolean.class);
    }

    public static JavaClassType Float() {
        return new JavaClassType(java.lang.Float.class);
    }

    public static Type String() {
        return new JavaClassType(java.lang.String.class);
    }
}
