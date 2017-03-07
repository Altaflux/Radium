package com.kubadziworski.bytecodegeneration.statement;


import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

public class ReturnStatementGenerator {
    private final InstructionAdapter methodVisitor;

    public ReturnStatementGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
        Expression expression = returnStatement.getExpression();
        Type type = expression.getType();
        expression.accept(generator);

        FunctionScope scope = generator.getScope();
        FunctionSignature functionSignature = scope.getCurrentFunctionSignature();
        if (functionSignature != null) {
            Type returnType = functionSignature.getReturnType();
            int returnOpCode = returnType.getAsmType().getOpcode(Opcodes.IRETURN);
            PrimitiveTypesWrapperFactory.coerce(returnType, type, methodVisitor);
            methodVisitor.visitInsn(returnOpCode);
        } else {
            int returnOpCode = type.getAsmType().getOpcode(Opcodes.IRETURN);
            methodVisitor.visitInsn(returnOpCode);
        }
    }
}