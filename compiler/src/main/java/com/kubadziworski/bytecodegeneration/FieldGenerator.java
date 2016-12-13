package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.domain.scope.Field;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

/**
 * Created by kuba on 13.05.16.
 */
public class FieldGenerator {


    private final ClassWriter classWriter;

    public FieldGenerator(ClassWriter classWriter) {
        this.classWriter = classWriter;
    }

    public void generate(Field field) {
        String name = field.getName();
        String descriptor = field.getType().getAsmType().getDescriptor();
        //classWriter.visitField(Opcodes.ACC_PUBLIC & Opcodes.ACC_STATIC, name,descriptor, null, null)
        FieldVisitor fieldVisitor = classWriter.visitField(field.getModifiers(), name,descriptor, null, null);
        fieldVisitor.visitEnd();
    }
}
