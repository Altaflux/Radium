package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.bytecodegeneration.util.ModifierTransformer;
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
        FieldVisitor fieldVisitor = cv.visitField(ModifierTransformer.transform(field.getModifiers()), name, descriptor, null, null);
        fieldVisitor.visitEnd();

        field.getGetterFunction().ifPresent(function -> methodGenerator.generatePropertyAccessor(function, field));
        field.getSetterFunction().ifPresent(function -> methodGenerator.generatePropertyAccessor(function, field));
    }
}
