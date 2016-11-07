package com.kubadziworski.bytecodegeneration.statement;


import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;

public class ReturnStatementGenerator {
    private final MethodVisitor methodVisitor;

    public ReturnStatementGenerator(MethodVisitor methodVisitor) {

        this.methodVisitor = methodVisitor;
    }

    public void generate(ReturnStatement returnStatement,StatementGenerator generator) {
        Expression expression = returnStatement.getExpression();
        Type type = expression.getType();
        expression.accept(generator);
        methodVisitor.visitInsn(type.getReturnOpcode());

    }
}