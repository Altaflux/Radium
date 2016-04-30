package com.kubadziworski.bytecodegenerator;

import java.util.List;
import java.util.Optional;

import com.kubadziworski.domain.global.CompareSign;
import com.kubadziworski.domain.expression.*;
import com.kubadziworski.domain.expression.math.*;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.*;
import com.kubadziworski.exception.BadArgumentsToFunctionCallException;
import com.kubadziworski.exception.CalledFunctionDoesNotExistException;
import com.kubadziworski.util.DescriptorFactory;
import com.kubadziworski.util.TypeResolver;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;

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
        String varName = varReference.geName();
        int index = scope.getLocalVariableIndex(varName);
        LocalVariable localVariable = scope.getLocalVariable(varName);
        Type type = localVariable.getType();
        methodVisitor.visitVarInsn(type.getLoadVariableOpcode(), index);
    }

    public void generate(FunctionParameter parameter) {
        Type type = parameter.getType();
        int index = scope.getLocalVariableIndex(parameter.getName());
        methodVisitor.visitVarInsn(type.getLoadVariableOpcode(), index);
    }

    public void generate(Value value) {
        Type type = value.getType();
        String stringValue = value.getValue();
        Object transformedValue = TypeResolver.getValueFromString(stringValue, type);
        methodVisitor.visitLdcInsn(transformedValue);
    }

    public void generate(FunctionCall functionCall) {
        String functionName = functionCall.getFunctionName();
        FunctionSignature signature = scope.getSignature(functionName);
        List<Expression> arguments = functionCall.getArguments();
        List<FunctionParameter> parameters = signature.getParameters();
        if (arguments.size() > parameters.size()) {
            throw new BadArgumentsToFunctionCallException(functionCall);
        }
        arguments.forEach(argument -> argument.accept(this));
        for (int i = arguments.size(); i < parameters.size(); i++) {
            Expression defaultParameter = parameters.get(i).getDefaultValue()
                    .orElseThrow(() -> new BadArgumentsToFunctionCallException(functionCall));
            defaultParameter.accept(this);
        }
        Type owner = functionCall.getOwner().orElse(new ClassType(scope.getClassName()));
        String methodDescriptor = getFunctionDescriptor(functionCall);
        String ownerDescriptor = owner.getInternalName();
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, ownerDescriptor, functionName, methodDescriptor, false);
    }

    public void generate(Addition expression) {
        if (expression.getType().equals(BultInType.STRING)) {
            generateStringAppend(expression);
            return;
        }
        evaluateArthimeticComponents(expression);
        Type type = expression.getType();
        methodVisitor.visitInsn(type.getAddOpcode());
    }

    public void generate(Substraction expression) {
        Type type = expression.getType();
        evaluateArthimeticComponents(expression);
        methodVisitor.visitInsn(type.getSubstractOpcode());
    }

    private void generateStringAppend(Addition expression) {
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
        methodVisitor.visitInsn(Opcodes.DUP);
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        Expression leftExpression = expression.getLeftExpression();
        leftExpression.accept(this);
        String leftExprDescriptor = leftExpression.getType().getDescriptor();
        String descriptor = "(" + leftExprDescriptor + ")Ljava/lang/StringBuilder;";
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        Expression rightExpression = expression.getRightExpression();
        rightExpression.accept(this);
        String rightExprDescriptor = rightExpression.getType().getDescriptor();
        descriptor = "(" + rightExprDescriptor + ")Ljava/lang/StringBuilder;";
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", descriptor, false);
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
    }

    public void generate(Multiplication expression) {
        evaluateArthimeticComponents(expression);
        Type type = expression.getType();
        methodVisitor.visitInsn(type.getMultiplyOpcode());
    }

    public void generate(Division expression) {
        evaluateArthimeticComponents(expression);
        Type type = expression.getType();
        methodVisitor.visitInsn(type.getDividOpcode());
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
        leftExpression.accept(this);
        rightExpression.accept(this);
        CompareSign compareSign = conditionalExpression.getCompareSign();
        Label endLabel = new Label();
        Label trueLabel = new Label();
        methodVisitor.visitJumpInsn(compareSign.getOpcode(), trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_0);
        methodVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        methodVisitor.visitLabel(trueLabel);
        methodVisitor.visitInsn(Opcodes.ICONST_1);
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
