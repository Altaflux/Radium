package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.node.expression.SuperCall;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

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
        Scope scope = block.getScope();
        MethodVisitor mv = classWriter.visitMethod(function.getModifiers(), name, description, null, null);
        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        block.accept(statementScopeGenerator);
        appendReturnIfNotExists(function, block, statementScopeGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    public void generate(Constructor constructor) {
        Block block = (Block) constructor.getRootStatement();
        Scope scope = block.getScope();
        String description = DescriptorFactory.getMethodDescriptor(constructor);
        MethodVisitor mv = classWriter.visitMethod(constructor.getModifiers(), "<init>", description, null, null);
        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        new SuperCall().accept(statementScopeGenerator);
        block.accept(statementScopeGenerator);
        appendReturnIfNotExists(constructor, block, statementScopeGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    private void appendReturnIfNotExists(Function function, Block block, StatementGenerator statementScopeGenerator) {
        boolean isLastStatementReturn = false;
        if (!block.getStatements().isEmpty()) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            isLastStatementReturn = lastStatement instanceof ReturnStatement;
        }
        if (!isLastStatementReturn) {
            EmptyExpression emptyExpression = new EmptyExpression(function.getReturnType());
            ReturnStatement returnStatement = new ReturnStatement(emptyExpression);
            returnStatement.accept(statementScopeGenerator);
        }
    }
}
