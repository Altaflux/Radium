package com.kubadziworski.bytecodegeneration.util;


import com.kubadziworski.domain.Modifiers;

import java.lang.reflect.Modifier;

public class ModifierTransformer {
    public static Modifiers transformJvm(int modifiers) {
        Modifiers modifiers1 = Modifiers.empty();
        if (Modifier.isFinal(modifiers)) {
            modifiers1 = modifiers1.with(com.kubadziworski.domain.Modifier.FINAL);
        }
        if (Modifier.isAbstract(modifiers)) {
            modifiers1 = modifiers1.with(com.kubadziworski.domain.Modifier.ABSTRACT);
        }
        if (Modifier.isProtected(modifiers)) {
            modifiers1 = modifiers1.with(com.kubadziworski.domain.Modifier.PROTECTED);
        }
        if (Modifier.isPublic(modifiers)) {
            modifiers1 = modifiers1.with(com.kubadziworski.domain.Modifier.PUBLIC);
        }
        if (Modifier.isPrivate(modifiers)) {
            modifiers1 = modifiers1.with(com.kubadziworski.domain.Modifier.PRIVATE);
        }
        if (Modifier.isStatic(modifiers)) {
            modifiers1 = modifiers1.with(com.kubadziworski.domain.Modifier.STATIC);
        }

        return modifiers1;
    }

    public static int transform(Modifiers modifiers) {
        int jvmModifiers = modifiers.getModifiers().stream().map(modifier -> {
            switch (modifier) {
                case ABSTRACT: {
                    return Modifier.ABSTRACT;
                }
                case PRIVATE: {
                    return Modifier.PRIVATE;
                }
                case PUBLIC: {
                    return Modifier.PUBLIC;
                }
                case PROTECTED: {
                    return Modifier.PROTECTED;
                }
                case STATIC: {
                    return Modifier.STATIC;
                }
                case FINAL: {
                    return Modifier.FINAL;
                }
                default: {
                    return 0;
                }
            }
        }).mapToInt(value -> value).sum();
        return jvmModifiers;
    }
}
