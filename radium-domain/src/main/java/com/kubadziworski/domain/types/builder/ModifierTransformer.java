package com.kubadziworski.domain.types.builder;

import com.kubadziworski.domain.types.Modifiers;

import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;

public class ModifierTransformer {

    public static Modifiers transformJvm(int modifiers) {

        Modifiers jvmModifiers = Modifiers.empty();
        if (Modifier.isFinal(modifiers)) {
            jvmModifiers = jvmModifiers.with(com.kubadziworski.domain.types.Modifier.FINAL);
        }
        if (Modifier.isAbstract(modifiers)) {
            jvmModifiers = jvmModifiers.with(com.kubadziworski.domain.types.Modifier.ABSTRACT);
        }
        if (Modifier.isProtected(modifiers)) {
            jvmModifiers = jvmModifiers.with(com.kubadziworski.domain.types.Modifier.PROTECTED);
        }
        if (Modifier.isPublic(modifiers)) {
            jvmModifiers = jvmModifiers.with(com.kubadziworski.domain.types.Modifier.PUBLIC);
        }
        if (Modifier.isPrivate(modifiers)) {
            jvmModifiers = jvmModifiers.with(com.kubadziworski.domain.types.Modifier.PRIVATE);
        }
        if (Modifier.isStatic(modifiers)) {
            jvmModifiers = jvmModifiers.with(com.kubadziworski.domain.types.Modifier.STATIC);
        }
        if ((modifiers & Opcodes.ACC_SYNTHETIC) != 0) {
            jvmModifiers = jvmModifiers.with(com.kubadziworski.domain.types.Modifier.SYNTHETIC);
        }

        return jvmModifiers;
    }

    public static int transform(Modifiers modifiers) {
        return modifiers.getModifiers().stream().map(modifier -> {
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
                case SYNTHETIC: {
                    return Opcodes.ACC_SYNTHETIC;
                }
                default: {
                    return 0;
                }
            }
        }).mapToInt(value -> value).sum();
    }
}
