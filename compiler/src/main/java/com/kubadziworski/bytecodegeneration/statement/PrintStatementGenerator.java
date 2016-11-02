package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.bytecodegeneration.expression.ExpressionGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.PrintStatement;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class PrintStatementGenerator {
    private final MethodVisitor methodVisitor;
    private final ExpressionGenerator expressionGenerator;

    public PrintStatementGenerator(ExpressionGenerator expressionGenerator, MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
        this.expressionGenerator = expressionGenerator;
    }

    public void generate(PrintStatement printStatement) {
        Expression expression = printStatement.getExpression();
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        expression.accept(expressionGenerator);
        Type type = expression.getType();

        if ((type.inheritsFrom(new JavaClassType("java.lang.Object"))) > 0) {
            type = new JavaClassType("java.lang.Object");
        }

        String descriptor = "(" + type.getDescriptor() + ")V";
        JavaClassType owner = new JavaClassType("java.io.PrintStream");
        String fieldDescriptor = owner.getDescriptor();
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, fieldDescriptor, "println", descriptor, false);
    }
}