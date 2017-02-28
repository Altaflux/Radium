package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.BlockExpression;
import com.kubadziworski.domain.node.expression.ConstructorCall;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.RadiumBuiltIns;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.NullType;
import com.kubadziworski.domain.type.intrinsic.VoidType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class BlockStatementGenerator {

    private final MethodVisitor methodVisitor;

    public BlockStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(BlockExpression block, StatementGenerator next) {
        generate(block.getStatementBlock(), false, next);
    }

    public void generate(Block block, StatementGenerator next) {
        generate(block, true, next);

    }

    private void generate(Block block, boolean notExpression, StatementGenerator next) {
        Scope newScope = block.getScope();
        List<Statement> statements = block.getStatements();

        StatementGenerator generator = new StatementGeneratorFilter(null, next, newScope);
        for (int x = 0; x < statements.size(); x++) {
            Statement stmt = statements.get(x);
            stmt.accept(generator);

            //Leave alive the last expression
            if (!notExpression && x == statements.size() - 1) {
                continue;
            }
            if (stmt instanceof Expression) {
                Type type = ((Expression) stmt).getType();
                if (!type.equals(VoidType.INSTANCE) && !type.equals(RadiumBuiltIns.NOTHING_TYPE)) {
                    if (!(stmt instanceof ConstructorCall)) {
                        visitPop(type);
                    }
                }
            }
        }

    }


    ///TODO this needs to be done better, maybe by defining a base call site,
    private void visitPop(Type type) {
        if (type.equals(NullType.INSTANCE)) {
            methodVisitor.visitInsn(Opcodes.POP);
            return;
        }
        switch (type.getAsmType().getSize()) {
            case 1: {
                methodVisitor.visitInsn(Opcodes.POP);
                break;
            }
            case 2: {
                methodVisitor.visitInsn(Opcodes.POP2);
                break;
            }
        }
    }
}