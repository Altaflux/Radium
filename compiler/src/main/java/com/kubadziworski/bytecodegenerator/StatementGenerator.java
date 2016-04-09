package com.kubadziworski.bytecodegenerator;

import com.kubadziworski.antlr.domain.scope.LocalVariable;
import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.expression.Expression;
import com.kubadziworski.antlr.domain.expression.Value;
import com.kubadziworski.antlr.domain.type.ClassType;
import com.kubadziworski.antlr.domain.type.BultInType;
import com.kubadziworski.antlr.domain.statement.PrintStatement;
import com.kubadziworski.antlr.domain.statement.Statement;
import com.kubadziworski.antlr.domain.statement.VariableDeclarationStatement;
import com.kubadziworski.antlr.domain.type.Type;
import org.abego.treelayout.internal.util.java.lang.string.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
        int index = scope.getLocalVariableIndex(name);
        if (expression instanceof Value) {
            Value value = (Value) expression;
            Type type = value.getType();
            String stringValue = value.getValue();
            if (type == BultInType.INT) {
                int val = Integer.parseInt(stringValue);
                methodVisitor.visitIntInsn(Opcodes.BIPUSH, val);
                methodVisitor.visitVarInsn(Opcodes.ISTORE, index);
            } else if (type == BultInType.STRING) {
                stringValue = StringUtils.removeStart(stringValue,"\"");
                stringValue = StringUtils.removeEnd(stringValue,"\"");
                methodVisitor.visitLdcInsn(stringValue);
                methodVisitor.visitVarInsn(Opcodes.ASTORE, index);
            }
        }
        scope.addLocalVariable(new LocalVariable(name,expression.getType()));
    }
}
