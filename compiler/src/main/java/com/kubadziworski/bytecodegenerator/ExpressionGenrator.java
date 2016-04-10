package com.kubadziworski.bytecodegenerator;

import java.util.Optional;

import com.kubadziworski.domain.expression.*;
import com.kubadziworski.domain.math.*;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassType;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.CalledFunctionDoesNotExistException;
import com.kubadziworski.util.DescriptorFactory;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created by kuba on 02.04.16.
 */
public class ExpressionGenrator {


    private MethodVisitor methodVisitor;
    private Scope scope;

    public ExpressionGenrator(MethodVisitor methodVisitor, Scope scope) {
        this.methodVisitor = methodVisitor;
        this.scope = scope;
    }

    public void generate(VarReference varReference) {
        String varName = varReference.getVarName();
        int index = scope.getLocalVariableIndex(varName);
        LocalVariable localVariable = scope.getLocalVariable(varName);
        Type type = localVariable.getType();
        if (type == BultInType.INT) {
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
        if (type == BultInType.INT) {
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

    private Optional<String> getDescriptorForFunctionOnClasspath(FunctionCall functionCall) {
        try {
            String functionName = functionCall.getFunctionName();
            Collection<Expression> parameters = functionCall.getParameters();
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
}
