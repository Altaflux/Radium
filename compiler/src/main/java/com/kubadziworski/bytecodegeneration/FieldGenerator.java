package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.domain.scope.Field;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class FieldGenerator {

    private final ClassVisitor cv;
    private final MethodGenerator methodGenerator;

    public FieldGenerator(ClassVisitor classWriter) {
        this.cv = classWriter;
        this.methodGenerator = new MethodGenerator(classWriter);
    }

    public void generate(Field field) {
        String name = field.getName();
        String descriptor = field.getType().getAsmType().getDescriptor();
        FieldVisitor fieldVisitor = cv.visitField(field.getModifiers(), name, descriptor, null, null);
        fieldVisitor.visitEnd();

        methodGenerator.generatePropertyAccessor(field.getGetterFunction(), field);
        methodGenerator.generatePropertyAccessor(field.getSetterFunction(), field);
    }
}
