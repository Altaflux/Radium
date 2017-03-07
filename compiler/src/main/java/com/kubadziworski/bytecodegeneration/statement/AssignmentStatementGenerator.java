package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.bytecodegeneration.util.PropertyAccessorsGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Assignment;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.scope.Variable;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.UnitType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

public class AssignmentStatementGenerator {

    private final InstructionAdapter methodVisitor;

    public AssignmentStatementGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Assignment assignment, FunctionScope scope, StatementGenerator generator) {
        Variable variable = assignment.getVariable();
        Expression expression = assignment.getAssignmentExpression();
        Type type = expression.getType();
        if (type.equals(VoidType.INSTANCE)) {
            expression = UnitType.chainExpression(expression);
        }

        int index = scope.getLocalVariableIndex(variable.getName());
        expression.accept(generator);
        castIfNecessary(type, variable.getType());
        methodVisitor.store(index, variable.getType().getAsmType());
    }

    public void generate(FieldAssignment assignment, FunctionScope scope, StatementGenerator generator) {
        PropertyAccessorsGenerator.generate(assignment, generator, methodVisitor);
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
