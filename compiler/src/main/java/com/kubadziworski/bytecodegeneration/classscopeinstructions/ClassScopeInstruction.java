package com.kubadziworski.bytecodegeneration.classscopeinstructions;


import org.objectweb.asm.MethodVisitor;

/**
 * Created by kuba on 23.03.16.
 */
public interface ClassScopeInstruction {
    void apply(MethodVisitor mv);
}
