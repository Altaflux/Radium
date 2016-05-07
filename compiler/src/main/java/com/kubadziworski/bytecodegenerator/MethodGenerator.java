package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.domain.classs.Constructor;
import com.kubadziworski.domain.classs.Function;
import com.kubadziworski.domain.expression.EmptyExpression;
import com.kubadziworski.domain.expression.SuperCall;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.Block;
import com.kubadziworski.domain.statement.ReturnStatement;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by kuba on 28.03.16.
 */
public class MethodGenerator {
    public static final String MAIN_FUN_NAME = "main";
    private final ClassWriter classWriter;

    public MethodGenerator(ClassWriter classWriter) {
        this.classWriter = classWriter;
    }

    public void generate(Function function) {
        String name = function.getName();
        boolean isMain = name.equals(MAIN_FUN_NAME);
        String description = DescriptorFactory.getMethodDescriptor(function);
        Block block = (Block) function.getRootStatement();
        Scope scope = block.getScope();
        int access = Opcodes.ACC_PUBLIC + (isMain ? Opcodes.ACC_STATIC : 0);
        MethodVisitor mv = classWriter.visitMethod(access, name, description, null, null);
        mv.visitCode();
        StatementGenerator statementScopeGenrator = new StatementGenerator(mv,scope);
        block.accept(statementScopeGenrator);
        appendReturnIfNotExists(function, block,statementScopeGenrator);
        mv.visitMaxs(-1,-1);
        mv.visitEnd();
    }

    public void generate(Constructor constructor) {
        Block block = (Block) constructor.getRootStatement();
        Scope scope = block.getScope();
        int access = Opcodes.ACC_PUBLIC;
        String description = DescriptorFactory.getMethodDescriptor(constructor);
        MethodVisitor mv = classWriter.visitMethod(access, "<init>", description, null, null);
        mv.visitCode();
        StatementGenerator statementScopeGenrator = new StatementGenerator(mv,scope);
        new SuperCall().accept(statementScopeGenrator);
        block.accept(statementScopeGenrator);
        appendReturnIfNotExists(constructor, block,statementScopeGenrator);
        mv.visitMaxs(-1,-1);
        mv.visitEnd();
    }

    private void appendReturnIfNotExists(Function function, Block block,StatementGenerator statementScopeGenrator) {
        boolean isLastStatementReturn = false;
        if(block.getStatements().size() >0) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            isLastStatementReturn = lastStatement instanceof ReturnStatement;
        }
        if(!isLastStatementReturn) {
            EmptyExpression emptyExpression = new EmptyExpression(function.getReturnType());
            ReturnStatement returnStatement = new ReturnStatement(emptyExpression);
            returnStatement.accept(statementScopeGenrator);
        }
    }
}
