package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.node.statement.TryCatchStatement;
import com.kubadziworski.domain.scope.Scope;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class TryCatchStatementGenerator {
    private final StatementGenerator statementGenerator;
    private final MethodVisitor methodVisitor;
    private final Scope scope;

    public TryCatchStatementGenerator(StatementGenerator statementGenerator, MethodVisitor methodVisitor, Scope scope) {
        this.statementGenerator = statementGenerator;
        this.methodVisitor = methodVisitor;
        this.scope = scope;
    }

    public void generate(TryCatchStatement tryCatchStatement) {

        Label lTryBlockStart = new Label();
        Label lTryBlockEnd = new Label();
        Label lCatchBlockEnd = new Label();


        boolean hasFinally = tryCatchStatement.getFinallyBlock().isPresent();


        methodVisitor.visitLabel(lTryBlockStart);
        Statement tryExpression = tryCatchStatement.getStatement();
        tryExpression.accept(statementGenerator);
        methodVisitor.visitJumpInsn(GOTO, lCatchBlockEnd);
        methodVisitor.visitLabel(lTryBlockEnd);

        tryCatchStatement.getCatchBlocks().forEach(catchBlock -> {
            Label catchStartLabel = new Label();
            methodVisitor.visitLabel(catchStartLabel);
            Scope catchBlockScope = catchBlock.getBlock().getScope();
            methodVisitor.visitVarInsn(ASTORE, catchBlockScope.getLocalVariableIndex(catchBlock.getParameter().getName()));
            catchBlock.getBlock().accept(statementGenerator);

            methodVisitor.visitTryCatchBlock(lTryBlockStart, lTryBlockEnd, catchStartLabel, catchBlock.getParameter().getType().getInternalName());
            methodVisitor.visitJumpInsn(GOTO, lCatchBlockEnd);
        });
    //TODO IMPLEMENT FINALLY BLOCK
//        if (hasFinally) {
//            tryCatchStatement.getFinallyBlock().ifPresent(statement -> {
//                Label finallyLabel = new Label();
//                methodVisitor.visitLabel(finallyLabel);
//                methodVisitor.visitLabel(lCatchBlockEnd);
//
//               // methodVisitor.visitVarInsn(ASTORE, statement.getScope().getLocalVariableIndex("$$"));
//                statement.accept(statementGenerator);
//
//             //   methodVisitor.visitVarInsn(ALOAD, statement.getScope().getLocalVariableIndex("$$"));
//              //  methodVisitor.visitInsn(ATHROW);
//
//                methodVisitor.visitTryCatchBlock(lTryBlockStart, lTryBlockEnd, finallyLabel, null);
//            });
//        } else {
//            methodVisitor.visitLabel(lCatchBlockEnd);
//        }
        methodVisitor.visitLabel(lCatchBlockEnd);
    }

}
