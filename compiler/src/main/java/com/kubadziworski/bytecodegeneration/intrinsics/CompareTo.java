package com.kubadziworski.bytecodegeneration.intrinsics;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.domain.type.intrinsic.primitive.AbstractPrimitiveType;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.commons.InstructionAdapter;

public class CompareTo extends IntrinsicMethod {

    @Override
    public Expression toExpression(CallableMember call, InstructionAdapter v) {

        return new IntrinsicExpression() {
            @Override
            public Type getType() {
                return call.getType();
            }

            @Override
            public void accept(StatementGenerator generator) {

                Type owner = call.getOwner().getType();
                if (owner instanceof TypeProjection) {
                    owner = ((TypeProjection) owner).getInternalType();
                }

                Type compareValue = call.getArguments().get(0).getType();
                if (compareValue instanceof TypeProjection) {
                    compareValue = ((TypeProjection) compareValue).getInternalType();
                }

                AbstractPrimitiveType topType = PrimitiveTypes.getBiggerDenominator((AbstractPrimitiveType) owner, (AbstractPrimitiveType) compareValue);
                org.objectweb.asm.Type asmTopType = topType.getAsmType();

                call.getOwner().accept(generator);
                PrimitiveTypesWrapperFactory.coerce(topType.getUnBoxedType(), owner, v);

                call.getArguments().get(0).accept(generator);
                PrimitiveTypesWrapperFactory.coerce(topType.getUnBoxedType(), compareValue, v);

                if (asmTopType.equals(org.objectweb.asm.Type.INT_TYPE) || asmTopType.equals(org.objectweb.asm.Type.SHORT_TYPE)
                        || asmTopType.equals(org.objectweb.asm.Type.CHAR_TYPE) || asmTopType.equals(org.objectweb.asm.Type.BOOLEAN_TYPE)) {
                    v.invokestatic("radium/jvm/internal/Intrinsics", "compare", "(II)I", false);
                } else if (asmTopType.equals(org.objectweb.asm.Type.LONG_TYPE)) {
                    v.invokestatic("radium/jvm/internal/Intrinsics", "compare", "(JJ)I", false);
                } else if (asmTopType.equals(org.objectweb.asm.Type.FLOAT_TYPE)) {
                    v.invokestatic("java/lang/Float", "compare", "(II)I", false);
                } else if (asmTopType.equals(org.objectweb.asm.Type.DOUBLE_TYPE)) {
                    v.invokestatic("java/lang/Double", "compare", "(II)I", false);
                } else {
                    throw new UnsupportedOperationException("Invalid types used for comparison");
                }
            }
        };
    }
}
