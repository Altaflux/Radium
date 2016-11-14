package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.PrintStatement;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class PrintStatementGenerator {
    private final MethodVisitor methodVisitor;

    public PrintStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(PrintStatement printStatement, StatementGenerator generator) {
        Expression expression = printStatement.getExpression();
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        expression.accept(generator);
        Type type = expression.getType();

        if (((type.inheritsFrom(ClassTypeFactory.createClassType("radium.Any"))) > 0) ||
                ((type.inheritsFrom(ClassTypeFactory.createClassType("java.lang.Object"))) > 0)) {
            type = ClassTypeFactory.createClassType("radium.Any");
        }

        String descriptor = "(" + type.getDescriptor() + ")V";
        Type owner = ClassTypeFactory.createClassType("java.io.PrintStream");
        String fieldDescriptor = owner.getDescriptor();
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, fieldDescriptor, "println", descriptor, false);

    }

}