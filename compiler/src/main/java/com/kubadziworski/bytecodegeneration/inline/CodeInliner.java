package com.kubadziworski.bytecodegeneration.inline;

import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.commons.InstructionAdapter;


public interface CodeInliner {

    void inlineMethod(Type currentClass, InstructionAdapter visitor, FunctionCall functionCall);
}
