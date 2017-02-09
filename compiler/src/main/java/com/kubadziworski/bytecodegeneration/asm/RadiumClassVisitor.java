package com.kubadziworski.bytecodegeneration.asm;

import com.kubadziworski.bytecodegeneration.inline.MethodInliner;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;


public class RadiumClassVisitor extends ClassVisitor {

    private String className;

    public RadiumClassVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className;
    }


    public final MethodVisitor visitMethod(final int access, final String name,
                                           final String desc, final String signature, final String[] exceptions) {

        MethodVisitor mvs = cv.visitMethod(access, name, desc, signature, exceptions);
        return new MethodInliner(access, desc, mvs, className);
    }
}
