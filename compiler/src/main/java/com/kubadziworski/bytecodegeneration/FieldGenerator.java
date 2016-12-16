package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.domain.scope.Field;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;

public class FieldGenerator {

    private final ClassWriter classWriter;
    private final MethodGenerator methodGenerator;

    public FieldGenerator(ClassWriter classWriter) {
        this.classWriter = classWriter;
        this.methodGenerator = new MethodGenerator(classWriter);
    }

    public void generate(Field field) {
        String name = field.getName();
        String descriptor = field.getType().getAsmType().getDescriptor();
        FieldVisitor fieldVisitor = classWriter.visitField(field.getModifiers(), name, descriptor, null, null);
        fieldVisitor.visitEnd();

        methodGenerator.generatePropertyAccessor(field.getGetterFunction(), field);
        methodGenerator.generatePropertyAccessor(field.getSetterFunction(), field);
    }
}
