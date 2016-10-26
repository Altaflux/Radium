package com.kubadziworski.domain;


import java.util.Arrays;


public enum UnarySign {

    NEGATION("!"),
    ADD("+"),
    SUB("-");

    private final String sign;


    UnarySign(String s) {
        this.sign = s;
    }

    public String getSign() {
        return sign;
    }

    public static UnarySign fromString(String sign) {
        return Arrays.stream(values()).filter(unarySign -> unarySign.sign.equals(sign))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sign not implemented"));
    }
}
