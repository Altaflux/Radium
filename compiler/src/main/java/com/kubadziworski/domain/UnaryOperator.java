package com.kubadziworski.domain;


import java.util.Arrays;

public enum UnaryOperator {

    INCREMENT("++"), DECREMENT("--");

    private final String operator;


    UnaryOperator(String operator) {
        this.operator = operator;
    }


    public static UnaryOperator fromString(String sign) {
        return Arrays.stream(values()).filter(opSign -> opSign.operator.equals(sign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sign not implemented"));
    }
}
