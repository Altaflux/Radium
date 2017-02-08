package com.kubadziworski.domain;


public class RadiumModifiers {

    /**
     * Return {@code true} if the integer argument includes the
     * {@code inline} modifier, {@code false} otherwise.
     *
     * @param mod a set of modifiers
     * @return {@code true} if {@code mod} includes the
     * {@code inline} modifier; {@code false} otherwise.
     */
    public static boolean isInline(int mod) {
        return (mod & INLINE) != 0;
    }

    public static final int INLINE = 0x00100000;
}
