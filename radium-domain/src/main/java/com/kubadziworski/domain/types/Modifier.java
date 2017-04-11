package com.kubadziworski.domain.types;


import java.util.HashMap;
import java.util.Map;

public enum Modifier {

    PUBLIC("public"),

    PROTECTED("protected"),

    PRIVATE("private"),

    INTERNAL("internal"),

    ANNOTATION("annotation"),

    ABSTRACT("abstract"),

    OPEN("open"),

    OVERRIDE("override"),

    STATIC("static"),

    INNER("inner"),

    INLINE("inline"),

    FINAL("final"),

    SYNTHETIC("synthetic");

    private final String str;

    private static Map<String, Modifier> constants = new HashMap<>();

    static {
        for (Modifier c : values()) {
            constants.put(c.str, c);
        }
    }

    Modifier(String str) {
        this.str = str;
    }

    public static Modifier fromValue(String value) {
        Modifier constant = constants.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }
}