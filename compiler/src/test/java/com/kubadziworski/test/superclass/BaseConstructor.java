package com.kubadziworski.test.superclass;

public class BaseConstructor {

    public BaseConstructor(String param) {
        System.out.println("Called base constructor: " + param);
    }

    public String foo() {
        System.out.println("Called base class");
        return "SUCCESS";
    }
}
