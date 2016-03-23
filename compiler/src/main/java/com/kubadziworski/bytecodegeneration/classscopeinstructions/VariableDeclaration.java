package com.kubadziworski.bytecodegeneration.classscopeinstructions;

import com.kubadziworski.antlr.EnkelLexer;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import com.kubadziworski.parsing.domain.Variable;

/**
 * Created by kuba on 15.03.16.
 */
public class VariableDeclaration implements ClassScopeInstruction,Opcodes {
    Variable variable;

    public VariableDeclaration(Variable variable) {
        this.variable = variable;
    }

    @Override
    public void apply(MethodVisitor mv) {
        final int type = variable.getType();
        if(type == EnkelLexer.NUMBER) {
            int val = Integer.parseInt(variable.getValue());
            mv.visitIntInsn(BIPUSH,val);
            mv.visitVarInsn(ISTORE,variable.getId());
        } else if(type == EnkelLexer.STRING) {
            mv.visitLdcInsn(variable.getValue());
            mv.visitVarInsn(ASTORE,variable.getId());
        }
    }
}
