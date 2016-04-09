package com.kubadziworski.bytecodegenerator;

import java.util.Optional;

import com.kubadziworski.antlr.domain.expression.VarReference;
import com.kubadziworski.antlr.domain.scope.LocalVariable;
import com.kubadziworski.antlr.domain.scope.Scope;
import com.kubadziworski.antlr.domain.expression.*;
import com.kubadziworski.antlr.domain.type.ClassType;
import com.kubadziworski.antlr.domain.expression.FunctionParameter;
import com.kubadziworski.antlr.domain.type.BultInType;
import com.kubadziworski.antlr.domain.type.Type;
import com.kubadziworski.exception.CalledFunctionDoesNotExistException;
import com.kubadziworski.utils.DescriptorFactory;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Created by kuba on 02.04.16.
 */
public class ExpressionGenrator {


    private MethodVisitor methodVisitor;

    public ExpressionGenrator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Expression expression, Scope scope) {
        if (expression instanceof VarReference) {
            VarReference varReference = (VarReference) expression;
            generate(varReference,scope);
        }
        if(expression instanceof Value) {
            Value value = (Value) expression;
            generate(value,scope);
        } else if(expression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) expression;
            generate(functionCall,scope);
        } else if(expression instanceof FunctionParameter) {
            FunctionParameter parameter = (FunctionParameter) expression;
            generate(parameter,scope);
        }
    }

    public void generate(VarReference varReference, Scope scope) {
        String varName = varReference.getVarName();
        int index = scope.getLocalVariableIndex(varName);
        LocalVariable localVariable = scope.getLocalVariable(varName);
        Type type = localVariable.getType();
        if(type == BultInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD,index);
        } else if(type == BultInType.STRING) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD,index);
        }
    }

    public void generate(FunctionParameter parameter, Scope scope) {
        Type type = parameter.getType();
        int index = scope.getLocalVariableIndex(parameter.getName());
        if(type == BultInType.INT) {
            methodVisitor.visitVarInsn(Opcodes.ILOAD,index);
        } else if(type == BultInType.STRING) {
            methodVisitor.visitVarInsn(Opcodes.ALOAD,index);
        }
    }

    public void generate(Value value, Scope scope) {
        Type type = value.getType();
        String stringValue = value.getValue();
        if (type == BultInType.INT) {
            int intValue = Integer.parseInt(stringValue);
            methodVisitor.visitIntInsn(Opcodes.BIPUSH, intValue);
        } else if (type == BultInType.STRING) {
            methodVisitor.visitLdcInsn(stringValue);
        }
    }

    public void generate(FunctionCall functionCall, Scope scope) {
        Collection<Expression> parameters = functionCall.getParameters();
        parameters.forEach(param -> generate(param, scope));
        Type owner = functionCall.getOwner().orElse(new ClassType(scope.getClassName()));
        String methodDescriptor = getFunctionDescriptor(functionCall,scope);
        String ownerDescriptor = owner.getInternalName();
        String functionName = functionCall.getFunctionName();
        methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, ownerDescriptor, functionName, methodDescriptor, false);
    }

    private String getFunctionDescriptor(FunctionCall functionCall, Scope scope) {
        return Optional.of(getDescriptorForFunctionInScope(functionCall, scope))
                .orElse(getDescriptorForFunctionOnClasspath(functionCall, scope))
                .orElseThrow(() -> new CalledFunctionDoesNotExistException(functionCall,scope));
    }


    private Optional<String> getDescriptorForFunctionInScope(FunctionCall functionCall, Scope scope) {
        return Optional.ofNullable(DescriptorFactory.getMethodDescriptor(functionCall.getSignature()));//TODO check errors here (not found function tec)
    }

    private Optional<String> getDescriptorForFunctionOnClasspath(FunctionCall functionCall, Scope scope) {
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
