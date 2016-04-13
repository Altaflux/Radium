package com.kubadziworski.bytecodegenerator;

import java.util.Optional;

import com.kubadziworski.CompareSign;
import com.kubadziworski.domain.expression.*;
import com.kubadziworski.domain.math.*;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.statement.PrintStatement;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.CalledFunctionDoesNotExistException;
import com.kubadziworski.exception.ComparisonBetweenDiferentTypesException;
import com.kubadziworski.util.DescriptorFactory;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created by kuba on 02.04.16.
 */
public class ExpressionGenrator {


    private final MethodVisitor methodVisitor;
    private final Scope scope;

    public ExpressionGenrator(MethodVisitor methodVisitor, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
    }

    public void generate(VarReference varReference) {
        String varName = varReference.getVarName();
        int index = scope.getLocalVariableIndex(varName);
        LocalVariable localVariable = scope.getLocalVariable(varName);
        Type type = localVariable.getType();
        if (type == BultInType.INT || type == BultInType.BOOLEAN) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, index);
        }
    }

    public void generate(FunctionParameter parameter) {
        Type type = parameter.getType();
        int index = scope.getLocalVariableIndex(parameter.getName());
        if (type == BultInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD, index);
        } else {
            methodVisitor.visitVarInsn(Opcodes.ALOAD, index);
        }
    }

    public void generate(Value value) {
        Type type = value.getType();
        String stringValue = value.getValue();
        if (type == BultInType.INT || type == BultInType.BOOLEAN) {
            int intValue = Integer.parseInt(stringValue);
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, intValue);
        } else if (type == BultInType.STRING) {
            stringValue = StringUtils.removeStart(stringValue, "\"");
            stringValue = StringUtils.removeEnd(stringValue, "\"");
            methodVisitor.visitLdcInsn(stringValue);
        }
    }

    public void generate(FunctionCall functionCall) {
        Collection<Expression> parameters = functionCall.getParameters();
        parameters.forEach(param -> param.accept(this));
        Type owner = functionCall.getOwner().orElse(new ClassType(scope.getClassName()));
        String methodDescriptor = getFunctionDescriptor(functionCall);
        String ownerDescriptor = owner.getInternalName();
        String functionName = functionCall.getFunctionName();
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, ownerDescriptor, functionName, methodDescriptor, false);
    }

    public void generate(Addition expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.IADD);
    }

    public void generate(Substraction expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.ISUB);
    }

    public void generate(Multiplication expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.IMUL);
    }

    public void generate(Division expression) {
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(Opcodes.IDIV);
    }

    private void evaluateArthimeticComponents(ArthimeticExpression expression) {
        Expression leftExpression = expression.getLeftExpression();
        Expression rightExpression = expression.getRightExpression();
        leftExpression.accept(this);
        rightExpression.accept(this);
    }

    private String getFunctionDescriptor(FunctionCall functionCall) {
        return Optional.of(getDescriptorForFunctionInScope(functionCall))
                .orElse(getDescriptorForFunctionOnClasspath(functionCall))
                .orElseThrow(() -> new CalledFunctionDoesNotExistException(functionCall));
    }


    private Optional<String> getDescriptorForFunctionInScope(FunctionCall functionCall) {
        return Optional.ofNullable(DescriptorFactory.getMethodDescriptor(functionCall.getSignature()));//TODO check errors here (not found function etc)
    }

    public void generate(ConditionalExpression conditionalExpression) {
        Expression leftExpression = conditionalExpression.getLeftExpression();
        Expression rightExpression = conditionalExpression.getRightExpression();
        Type type = leftExpression.getType();
        if(type != rightExpression.getType()) {
            throw new ComparisonBetweenDiferentTypesException(leftExpression, rightExpression);
        }
        leftExpression.accept(this);
        rightExpression.accept(this);
        CompareSign compareSign = conditionalExpression.getCompareSign();
        Label endLabel = new Label();
        Label falseLabel = new Label();
        methodVisitor.visitJumpInsn(compareSign.getOpcode(),falseLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_1);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(falseLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_0);
        methodVisitor.visitLabel(endLabel);
    }

    private Optional<String> getDescriptorForFunctionOnClasspath(FunctionCall functionCall) {
        try {
            String functionName = functionCall.getFunctionName();
            Optional<Type> owner = functionCall.getOwner();
            String className = owner.isPresent() ? owner.get().getName() : scope.getClassName();
            Class<?> aClass = Class.forName(className);
            Method method = aClass.getMethod(functionName);
            String methodDescriptor = org.objectweb.asm.Type.getMethodDescriptor(method);
            return Optional.of(methodDescriptor);
        } catch (ReflectiveOperationException e) {
            return Optional.empty();
        }
    }

    public void generate(EmptyExpression emptyExpression) {
        //do nothing ;)
    }
}
