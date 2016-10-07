package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.expression.ConstructorCall;
import com.kubadziworski.domain.node.expression.Expression;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.type.BultInType;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.List;

public class BlockStatementGenerator {
    private final MethodVisitor methodVisitor;

    public BlockStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(Block block) {
        Scope newScope = block.getScope();
        List<Statement> statements = block.getStatements();
        StatementGenerator statementGenerator = new StatementGenerator(methodVisitor, newScope);
        statements.forEach(stmt -> {
            stmt.accept(statementGenerator);
            if (stmt instanceof Expression) {
                if (!((Expression) stmt).getType().equals(BultInType.VOID)) {
                    if (!(stmt instanceof ConstructorCall)) {
                        methodVisitor.visitInsn(Opcodes.POP);
                    }
                }
            }
        });
    }
}