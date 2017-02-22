package com.kubadziworski.domain;


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

    FINAL("final");

    private final String str;

    Modifier(String str) {
        this.str = str;
    }

}
