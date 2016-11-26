package com.kubadziworski.bytecodegeneration.statement;


import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.InstructionAdapter;

public class ReturnStatementGenerator {
    private final MethodVisitor methodVisitor;

    public ReturnStatementGenerator(MethodVisitor methodVisitor) {

        this.methodVisitor = methodVisitor;
    }

    public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
        Expression expression = returnStatement.getExpression();
        Type type = expression.getType();
        expression.accept(generator);

        Scope scope = generator.getScope();
        FunctionSignature functionSignature = scope.getCurrentFunctionSignature();
        if (functionSignature != null) {
            Type returnType = functionSignature.getReturnType();
            PrimitiveTypesWrapperFactory.coerce(returnType, type, new InstructionAdapter(methodVisitor));
            methodVisitor.visitInsn(returnType.getReturnOpcode());
        } else {
            methodVisitor.visitInsn(type.getReturnOpcode());
        }
    }
}