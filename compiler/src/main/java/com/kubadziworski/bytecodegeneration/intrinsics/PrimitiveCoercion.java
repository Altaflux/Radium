package com.kubadziworski.bytecodegeneration.intrinsics;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.primitive.PrimitiveTypes;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.InstructionAdapter;


public class PrimitiveCoercion extends IntrinsicMethod {

    @Override
    public Expression toExpression(CallableMember functionCall, MethodVisitor methodVisitor) {
        return new IntrinsicExpression() {

            @Override
            public Type getType() {
                return functionCall.getType();
            }

            @Override
            public void accept(StatementGenerator generator) {
                switch (functionCall.getName()) {
                    case "toInt": {
                        coerce(functionCall.getOwner(), methodVisitor, generator, PrimitiveTypes.INT_TYPE);
                        break;
                    }
                    case "toLong": {
                        coerce(functionCall.getOwner(), methodVisitor, generator, PrimitiveTypes.LONG_TYPE);
                        break;
                    }
                    case "toFloat": {
                        coerce(functionCall.getOwner(), methodVisitor, generator, PrimitiveTypes.FLOAT_TYPE);
                        break;
                    }
                    case "toDouble": {
                        coerce(functionCall.getOwner(), methodVisitor, generator, PrimitiveTypes.DOUBLE_TYPE);
                        break;
                    }
                    case "toChar": {
                        coerce(functionCall.getOwner(), methodVisitor, generator, PrimitiveTypes.CHAR_TYPE);
                        break;
                    }
                    case "toByte": {
                        coerce(functionCall.getOwner(), methodVisitor, generator, PrimitiveTypes.BYTE_TYPE);
                        break;
                    }
                    case "toShort": {
                        coerce(functionCall.getOwner(), methodVisitor, generator, PrimitiveTypes.SHORT_TYPE);
                        break;
                    }
                }
            }

            private void coerce(Expression value, MethodVisitor v, StatementGenerator generator, Type primitiveType) {
                InstructionAdapter ad = new InstructionAdapter(v);
                value.accept(generator);
                PrimitiveTypesWrapperFactory.coerce(primitiveType, value.getType(), ad);
            }
        };
    }
}
