package com.kubadziworski.bytecodegeneration.expression;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.domain.node.expression.Argument;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import org.objectweb.asm.commons.InstructionAdapter;


public class ArgumentStatementGenerator {

    private InstructionAdapter methodVisitor;

    public ArgumentStatementGenerator(InstructionAdapter methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Argument argument, StatementGenerator generator) {
        argument.getExpression().accept(generator);
        PrimitiveTypesWrapperFactory
                .coerce(argument.getReceiverType(), argument.getType(), methodVisitor);
    }
}
