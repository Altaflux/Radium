package com.kubadziworski.domain;

import com.kubadziworski.bytecodegeneration.MethodGenerator;
import com.kubadziworski.domain.scope.FunctionSignature;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.BultInType;
import com.kubadziworski.domain.type.Type;

/**
 * Created by kuba on 07.05.16.
 */
public class Constructor extends Function {
    public Constructor(FunctionSignature signature, Statement block) {
        super(signature, block);
    }

    @Override
    public Type getReturnType() {
        return BultInType.VOID;
    }

    @Override
    public void accept(MethodGenerator generator) {
        generator.generate(this);
    }
}
