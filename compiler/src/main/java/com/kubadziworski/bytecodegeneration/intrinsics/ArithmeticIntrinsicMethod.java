package com.kubadziworski.bytecodegeneration.intrinsics;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.ArithmeticOperator;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.arthimetic.PureArithmeticExpression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.List;


public class ArithmeticIntrinsicMethod extends IntrinsicMethod {


    public Expression toExpression(CallableMember functionCall, InstructionAdapter methodVisitor) {
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
                new PureArithmeticExpression(owner, arguments.get(0), functionCall.getType(), operator).accept(generator);
            }
        };
    }
}
