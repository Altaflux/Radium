package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.domain.expression.FunctionCall;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.expression.Expression;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.statement.PrintStatement;
import com.kubadziworski.domain.statement.Statement;
import com.kubadziworski.domain.statement.VariableDeclarationStatement;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by kuba on 29.03.16.
 */
public class StatementGenerator {

    private MethodVisitor methodVisitor;
    private ExpressionGenrator expressionGenrator;
    private Scope scope;

    public StatementGenerator(MethodVisitor methodVisitor, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
        this.expressionGenrator = new ExpressionGenrator(methodVisitor,scope);
    }

    public void generate(PrintStatement printStatement) {
        Expression expression = printStatement.getExpression();
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        expression.accept(expressionGenrator);
        Type type = expression.getType();
        String descriptor = "(" + type.getDescriptor() + ")V";
        ClassType owner = new ClassType("java.io.PrintStream");
        String fieldDescriptor = owner.getDescriptor();
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, fieldDescriptor, "println", descriptor, false);
    }

    public void generate(VariableDeclarationStatement variableDeclarationStatement) {
        Expression expression = variableDeclarationStatement.getExpression();
        String name = variableDeclarationStatement.getName();
        int index = scope.getLocalVariableIndex(name);
        Type type = expression.getType();
        expression.accept(expressionGenrator);
        if (type == BultInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
        }
        scope.addLocalVariable(new LocalVariable(name, expression.getType()));
    }

    public void generate(FunctionCall functionCall) {
        functionCall.accept(expressionGenrator);
    }
}
