package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.expression.Identifier;
import com.kubadziworski.antlr.domain.expression.Value;
import com.kubadziworski.antlr.domain.type.ClassType;
import com.kubadziworski.antlr.domain.type.BultInType;
import com.kubadziworski.antlr.domain.statement.PrintStatement;
import com.kubadziworski.antlr.domain.statement.Statement;
import com.kubadziworski.antlr.domain.statement.VariableDeclarationStatement;
import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.bytecodegenerator.domain.Variable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuba on 29.03.16.
 */
public class StatementGenerator {

    private MethodVisitor methodVisitor;
    private ExpressionGenrator expressionGenrator;

    public StatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
        expressionGenrator = new ExpressionGenrator(methodVisitor);
    }

    public void generate(Statement expression, Scope scope) {
        if (expression instanceof PrintStatement) {
            PrintStatement printStatement = (PrintStatement) expression;
            generate(printStatement, scope);
        } else if (expression instanceof VariableDeclarationStatement) {
            VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) expression;
            generate(variableDeclarationStatement, scope);
        } else if (expression instanceof Expression) {
            expressionGenrator.generate((Expression) expression,scope);
        }
    }

    public void generate(PrintStatement printStatement, Scope scope) {
        ExpressionGenrator expressionGenrator = new ExpressionGenrator(methodVisitor);
        Expression expression = printStatement.getExpression();
        methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        expressionGenrator.generate(expression, scope);
        Type type = expression.getType();
        String descriptor = "(" + type.getDescriptor() + ")V";
        ClassType owner = new ClassType("java.io.PrintStream");
        String fieldDescriptor = owner.getDescriptor();
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,fieldDescriptor, "println", descriptor, false);
    }

    public void generate(VariableDeclarationStatement variableDeclarationStatement, Scope scope) {
        Expression expression = variableDeclarationStatement.getExpression();
        String name = variableDeclarationStatement.getName();
        if (expression instanceof Value) {
            Value value = (Value) expression;
            Type type = value.getType();
            String stringValue = value.getValue();
            if (type == BultInType.INT) {
                int val = Integer.parseInt(stringValue);
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, val);
                methodVisitor.visitVarInsn(Opcodes.ISTORE, 0);
            } else if (type == BultInType.STRING) {
                methodVisitor.visitLdcInsn(value);
                methodVisitor.visitVarInsn(Opcodes.ASTORE, 0);
            }
        }
        scope.addIdentifier(new Identifier(name,expression));
    }
}
