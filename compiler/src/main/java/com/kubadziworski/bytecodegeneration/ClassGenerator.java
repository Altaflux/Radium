package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.bytecodegeneration.asm.RadiumClassVisitor;
import com.kubadziworski.domain.ClassDeclaration;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.MetaDataBuilder;
import com.kubadziworski.domain.scope.Field;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.util.Collection;
import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class ClassGenerator {

    private static final int CLASS_VERSION = 52;
    private final ClassWriter classWriter;
    private final MetaDataBuilder metaDataBuilder = new MetaDataBuilder();

    public ClassGenerator() {

        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    }

    public ClassWriter generate(ClassDeclaration classDeclaration) {
        String name = classDeclaration.getClassType().getAsmType().getInternalName();

        RadiumClassVisitor visitor = new RadiumClassVisitor(Opcodes.ASM5, classWriter, classDeclaration.getClassType().getAsmType().getClassName());
        visitor.visit(CLASS_VERSION, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, name, null, "java/lang/Object", null);
        List<Function> methods = classDeclaration.getMethods();
        Collection<Field> fields = classDeclaration.getFields();
        FieldGenerator fieldGenerator = new FieldGenerator(visitor);
        fields.forEach(f -> f.accept(fieldGenerator));
        MethodGenerator methodGenerator = new MethodGenerator(visitor);
        methods.forEach(f -> f.accept(methodGenerator));

        AnnotationVisitor av0 = classWriter.visitAnnotation("Lradium/internal/Metadata;", true);
        av0.visit("data", metaDataBuilder.toString(metaDataBuilder.classMetadata(classDeclaration)));
        visitor.visitEnd();

        return classWriter;
    }


}
