package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.scope.Variable;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.exception.IncompatibleTypesException;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.reflect.Modifier;

public class AssignmentStatementGenerator {
    private final InstructionAdapter methodVisitor;


    public AssignmentStatementGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Assignment assignment, Scope scope, StatementGenerator generator) {
        Variable variable = assignment.getVariable();
        Expression expression = assignment.getAssignmentExpression();
        Type type = expression.getType();
        if (type.equals(VoidType.INSTANCE)) {
            expression = UnitType.chainExpression(expression);
        }

        validateType(expression, variable);
        int index = scope.getLocalVariableIndex(variable.getName());
        expression.accept(generator);
        castIfNecessary(type, variable.getType());
        methodVisitor.store(index, variable.getType().getAsmType());
    }

    public void generate(FieldAssignment assignment, Scope scope, StatementGenerator generator) {
        Variable field = assignment.getField();
        Expression expression = assignment.getAssignmentExpression();
        Type type = expression.getType();
        if (type.equals(VoidType.INSTANCE)) {
            expression = UnitType.chainExpression(expression);
        }
        validateType(expression, field);
        assignment.getPreExpression().accept(generator);

        expression.accept(generator);
        castIfNecessary(type, field.getType());

        int opCode = Modifier.isStatic(((Field) field).getModifiers()) ? Opcodes.PUTSTATIC : Opcodes.PUTFIELD;
        methodVisitor.visitFieldInsn(opCode, ((Field) field).getOwnerInternalName(), field.getName(),
                field.getType().getAsmType().getDescriptor());
    }


    private void validateType(Expression expression, Variable variable) {
        if (expression.getType().inheritsFrom(variable.getType()) < 0) {
            throw new IncompatibleTypesException(variable.getName(), variable.getType(), expression.getType());
        }
    }

    private void castIfNecessary(Type expressionType, Type variableType) {
        PrimitiveTypesWrapperFactory.coerce(variableType, expressionType, methodVisitor);

        if (!expressionType.equals(variableType)) {
            if (!variableType.isPrimitive()) {
                methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, variableType.getAsmType().getInternalName());
            }
        }
    }
}
