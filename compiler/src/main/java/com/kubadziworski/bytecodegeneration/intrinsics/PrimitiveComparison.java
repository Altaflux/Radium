package com.kubadziworski.bytecodegeneration.intrinsics;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.CompareSign;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.domain.type.intrinsic.primitive.function.PrimitiveFunction;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.commons.InstructionAdapter;

/**
 * Created by plozano on 12/2/2016.
 */
public class PrimitiveComparison extends IntrinsicMethod {

    @Override
    public Expression toExpression(CallableMember call, InstructionAdapter v) {
        return new IntrinsicExpression() {
            @Override
            public Type getType() {
                return call.getType();
            }

            @Override
            public void accept(StatementGenerator generator) {

                CompareSign compareSign = CompareSign.fromString(call.getName());

                Type owner = call.getOwner().getType();
                if (owner instanceof TypeProjection) {
                    owner = ((TypeProjection) owner).getInternalType();
                }
                Type compareValue = call.getArguments().get(0).getType();
                if (compareValue instanceof TypeProjection) {
                    compareValue = ((TypeProjection) compareValue).getInternalType();
                }
                Type topType = PrimitiveTypes.getBiggerDenominator((AbstractPrimitiveType) owner, (AbstractPrimitiveType) compareValue);
                org.objectweb.asm.Type asmTopType = topType.getAsmType();
                call.getOwner().accept(generator);
                PrimitiveTypesWrapperFactory.coerce(topType, owner, v);

                //We need to extract the expression of the argument as the argumentGenerator will try to coerce it
                Argument argument = call.getArguments().get(0);
                argument.getExpression().accept(generator);

                PrimitiveTypesWrapperFactory.coerce(topType, compareValue, v);

                PrimitiveFunction.comparePrimitives(asmTopType, compareSign, v);
            }
        };
    }
}
