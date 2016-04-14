package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.domain.expression.EmptyExpression;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.Block;
import com.kubadziworski.domain.statement.ReturnStatement;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import com.kubadziworski.domain.classs.Function;

/**
 * Created by kuba on 28.03.16.
 */
public class MethodGenerator {

    private final ClassWriter classWriter;

    public MethodGenerator(ClassWriter classWriter) {
        this.classWriter = classWriter;
    }

    public void generate(Function function) {
        String name = function.getName();
        String description = DescriptorFactory.getMethodDescriptor(function);
        Block block = (Block) function.getRootStatement();
        int access = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC;//(function.getName().equals("main") ? Opcodes.ACC_STATIC : 0);
        MethodVisitor mv = classWriter.visitMethod(access, name, description, null, null);
        mv.visitCode();
        Scope scope = block.getScope();
        StatementGenerator statementScopeGenrator = new StatementGenerator(mv,scope);
        block.accept(statementScopeGenrator);
        appendReturnIfNotExists(function, block,statementScopeGenrator);
        mv.visitMaxs(-1,-1);
        mv.visitEnd();
    }

    private void appendReturnIfNotExists(Function function, Block block,StatementGenerator statementScopeGenrator) {
        Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
        boolean isLastStatementReturn = lastStatement instanceof ReturnStatement;
        if(!isLastStatementReturn) {
            EmptyExpression emptyExpression = new EmptyExpression(function.getReturnType());
            ReturnStatement returnStatement = new ReturnStatement(emptyExpression);
            returnStatement.accept(statementScopeGenrator);
        }
    }
}
