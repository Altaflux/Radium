package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.domain.classs.Function;
import com.kubadziworski.domain.global.ClassDeclaration;
import com.kubadziworski.domain.type.ClassType;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

/**
 * Created by kuba on 28.03.16.
 */
public class ClassGenerator {

    private static final int CLASS_VERSION = 52;
    private ClassWriter classWriter;

    public ClassGenerator() {
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    }

    public ClassWriter generate(ClassDeclaration classDeclaration) {
        String name = classDeclaration.getName();
        classWriter.visit(CLASS_VERSION, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,name,null,"java/lang/Object",null);
        List<Function> methods = classDeclaration.getMethods();
        MethodGenerator methodGenerator = new MethodGenerator(classWriter);
        methods.forEach(f ->f.accept(methodGenerator));
        classWriter.visitEnd();
        return classWriter;
    }
}
