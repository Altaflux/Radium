package com.kubadziworski.bytecodegeneration.instructions;

import org.objectweb.asm.MethodVisitor;

/**
 * Created by kuba on 15.03.16.
 */
public interface Instruction {
    void apply(MethodVisitor methodVisitor);
}
