package com.kubadziworski.bytecodegeneration.util;


import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.node.expression.*;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import com.kubadziworski.util.PropertyAccessorsUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.Collections;
import java.util.Optional;

public class PropertyAccessorsGenerator {

    public static void generateNoPropertyTransformation(FieldAssignment assignment, StatementGenerator generator, InstructionAdapter adapter) {
        generate(assignment, generator, adapter, false);
    }

    public static void generate(FieldAssignment assignment, StatementGenerator generator, InstructionAdapter adapter) {
        generate(assignment, generator, adapter, true);
    }

    public static void generateNoPropertyTransformation(FieldReference fieldReference, StatementGenerator expressionGenerator, InstructionAdapter adapter) {
        generate(fieldReference, expressionGenerator, adapter, false);
    }

    public static void generate(FieldReference fieldReference, StatementGenerator expressionGenerator, InstructionAdapter adapter) {
        generate(fieldReference, expressionGenerator, adapter, true);
    }

    private static void generate(FieldAssignment assignment, StatementGenerator generator, InstructionAdapter adapter, boolean useAccessors) {
        Field field = assignment.getField();

        if (useAccessors) {
            Optional<FunctionSignature> signature = PropertyAccessorsUtil.getSetterFunctionSignatureForField(field);
            Optional<FunctionCall> functionCall = signature.map(functionSignature -> {
                ArgumentHolder argument = new ArgumentHolder(assignment.getAssignmentExpression(), null);
                return new PropertyAccessorCall(functionSignature, functionSignature.createArgumentList(Collections.singletonList(argument)),
                        assignment.getPreExpression(), field);
            });
            //The parsing visitors should have guaranteed that if this is not possible, at least by direct field access should work
            if (functionCall.isPresent() && PropertyAccessorsUtil.isFunctionAccessible(functionCall.get().getSignature(), generator.getScope().getClassType())) {
                functionCall.get().accept(generator);
                return;
            }
        }


        Expression expression = assignment.getAssignmentExpression();
        Type type = expression.getType();
        if (type.equals(VoidType.INSTANCE)) {
            expression = UnitType.chainExpression(expression);
        }

        assignment.getPreExpression().accept(generator);
        expression.accept(generator);
        castIfNecessary(type, field.getType(), adapter);

        int opCode = field.getModifiers().contains(Modifier.STATIC) ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD;
        adapter.visitFieldInsn(opCode, field.getOwner().getAsmType().getInternalName(), field.getName(),
                field.getType().getAsmType().getDescriptor());
    }


    private static void generate(FieldReference fieldReference, StatementGenerator generator, InstructionAdapter adapter, boolean useAccessors) {
        Field field = fieldReference.getField();

        if (useAccessors) {
            Optional<FunctionCall> functionCall = PropertyAccessorsUtil.getGetterFunctionSignatureForField(field)
                    .map(functionSignature -> new PropertyAccessorCall(functionSignature, fieldReference.getOwner(), field));
            //The parsing visitors should have guaranteed that if this is not possible, at least by direct field access should work
            if (functionCall.isPresent() && PropertyAccessorsUtil.isFunctionAccessible(functionCall.get().getSignature(), generator.getScope().getClassType())) {
                functionCall.get().accept(generator);
                return;
            }
        }


        Type type = fieldReference.getType();
        String ownerInternalName = fieldReference.getField().getOwner().getAsmType().getInternalName();
        String descriptor = type.getAsmType().getDescriptor();
        Expression owner = fieldReference.getOwner();
        owner.accept(generator);

        int opCode = fieldReference.getField().getModifiers().contains(Modifier.STATIC) ? Opcodes.GETSTATIC : Opcodes.GETFIELD;
        adapter.visitFieldInsn(opCode, ownerInternalName, field.getName(), descriptor);
    }


    private static void castIfNecessary(Type expressionType, Type variableType, InstructionAdapter adapter) {
        PrimitiveTypesWrapperFactory.coerce(variableType, expressionType, adapter);
        if (!expressionType.equals(variableType)) {
            if (!variableType.isPrimitive()) {
                adapter.visitTypeInsn(Opcodes.CHECKCAST, variableType.getAsmType().getInternalName());
            }
        }
    }
}
