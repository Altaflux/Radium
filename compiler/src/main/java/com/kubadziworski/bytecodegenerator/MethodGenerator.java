package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import com.kubadziworski.domain.classs.Function;

import java.util.Collection;

/**
 * Created by kuba on 28.03.16.
 */
public class MethodGenerator {

    private final ClassWriter classWriter;

    public MethodGenerator(ClassWriter classWriter) {
        this.classWriter = classWriter;
    }

    public void generate(Function function) {
        Scope scope = function.getScope();
        String name = function.getName();
        String description = DescriptorFactory.getMethodDescriptor(function);
        Collection<Statement> instructions = function.getStatements();
        int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;//(function.getName().equals("main") ? Opcodes.ACC_STATIC : 0);
        MethodVisitor mv = classWriter.visitMethod(access, name, description, null, null);
        mv.visitCode();
        StatementGenerator statementScopeGenrator = new StatementGenerator(mv,scope);
        instructions.forEach(instruction -> instruction.accept(statementScopeGenrator));
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(-1,-1);
        mv.visitEnd();
    }
}
