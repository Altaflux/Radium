package com.kubadziworski.bytecodegeneration.intrinsics;

import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.expression.function.FunctionCall;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.Optional;


public class IntrinsicMethodCaller extends StatementGeneratorFilter {

    private static ThreadLocal<IntrinsicMethods> intrinsicMethods = ThreadLocal.withInitial(IntrinsicMethods::new);
    private final InstructionAdapter adapter;

    public IntrinsicMethodCaller(InstructionAdapter adapter, StatementGenerator parent, StatementGenerator next, Scope scope) {
        super(parent, next, scope);
        this.adapter = adapter;
    }

    public void generate(FunctionCall functionCall, StatementGenerator statementGenerator) {
        Optional<Expression> intrinsicExpression = callArithmeticExpression(functionCall);
        if (intrinsicExpression.isPresent()) {
            intrinsicExpression.get().accept(statementGenerator);
            return;
        }
        next.generate(functionCall, statementGenerator);
    }


    private Optional<Expression> callArithmeticExpression(FunctionCall functionCall) {
        return intrinsicMethods.get().intrinsicMethod(functionCall).map(intrinsicMethod -> intrinsicMethod.toExpression(functionCall, adapter));
    }

    public StatementGenerator copy(StatementGenerator parent) {
        return new IntrinsicMethodCaller(adapter, parent, this.next, getScope());
    }
}
