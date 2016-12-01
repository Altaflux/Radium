package com.kubadziworski.bytecodegeneration.intrinsics;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.List;


public class ArithmeticIntrinsicMethod extends IntrinsicMethod {


    public Expression toExpression(CallableMember functionCall, InstructionAdapter instructionAdapter) {
        return new IntrinsicExpression() {

            @Override
            public Type getType() {
                return functionCall.getType();
            }

            @Override
            public void accept(StatementGenerator generator) {
                Expression owner = functionCall.getOwner();
                ArithmeticOperator operator = ArithmeticOperator.fromMethodName(functionCall.getName());
                List<Argument> arguments = functionCall.getArguments();

                owner.accept(generator);
                PrimitiveTypesWrapperFactory.coerce(functionCall.getType(), owner.getType(), instructionAdapter);

                Expression rightExpression = arguments.get(0);
                rightExpression.accept(generator);
                PrimitiveTypesWrapperFactory.coerce(functionCall.getType(), rightExpression.getType(), instructionAdapter);
                instructionAdapter.visitInsn(operator.getOperationOpCode(functionCall.getType()));
            }
        };
    }
}
