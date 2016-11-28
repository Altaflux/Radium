package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.DupExpression;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.NotNullCastExpression;
import com.kubadziworski.domain.type.BoxableType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;


public class NotNullCastExpressionGenerator {

    private final MethodVisitor methodVisitor;

    public NotNullCastExpressionGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(NotNullCastExpression castExpression, StatementGenerator generator) {
        Expression expression = castExpression.getExpression();


        Type type = expression.getType();
        if (type instanceof TypeProjection) {
            type = ((TypeProjection) type).getInternalType();
        }

        if (type.isPrimitive() && !((BoxableType) type).isBoxed()) {
            expression.accept(generator);
            return;
        }

        DupExpression dupExpression = new DupExpression(expression, 0);
        dupExpression.accept(generator);

        Label postNullCheck = new Label();
        methodVisitor.visitJumpInsn(IFNONNULL, postNullCheck);
        methodVisitor.visitTypeInsn(NEW, "java/lang/RuntimeException");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitLdcInsn("Object is for type: " + expression.getType() + " is null");
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(ATHROW);

        methodVisitor.visitLabel(postNullCheck);

    }
}
