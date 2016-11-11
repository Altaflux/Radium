package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.node.statement.TryCatchStatement;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.GOTO;

public class TryCatchStatementGenerator {

    private final MethodVisitor methodVisitor;

    public TryCatchStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(TryCatchStatement tryCatchStatement, StatementGenerator statementGenerator) {

        Label lTryBlockStart = new Label();
        Label lTryBlockEnd = new Label();
        Label lCatchBlockEnd = new Label();


        ReturnStatementAlterer finalGenerator = new ReturnStatementAlterer(lTryBlockStart, null, statementGenerator, statementGenerator.getScope());

        methodVisitor.visitLabel(lTryBlockStart);
        Statement tryExpression = tryCatchStatement.getStatement();

        boolean hasFinally = tryCatchStatement.getFinallyBlock().isPresent();

        tryExpression.accept(finalGenerator);
        if (!tryExpression.isReturnComplete()) {
            methodVisitor.visitJumpInsn(GOTO, lCatchBlockEnd);
        }
        methodVisitor.visitLabel(lTryBlockEnd);

        if (finalGenerator.labelPackses.isEmpty()) {
            finalGenerator.labelPackses.add(new LabelPacks(lTryBlockStart, lTryBlockEnd));
        }

        List<CatchPack> catchLabels = tryCatchStatement.getCatchBlocks().stream().map(catchBlock -> {
            Label catchStartLabel = new Label();
            methodVisitor.visitLabel(catchStartLabel);
            Scope catchBlockScope = catchBlock.getBlock().getScope();
            methodVisitor.visitVarInsn(ASTORE, catchBlockScope.getLocalVariableIndex(catchBlock.getParameter().getName()));
            catchBlock.getBlock().accept(statementGenerator);

            if (!catchBlock.isReturnComplete()) {
                methodVisitor.visitJumpInsn(GOTO, lCatchBlockEnd);
            }
            return new CatchPack(catchStartLabel, catchBlock.getParameter().getType());
        }).collect(Collectors.toList());


        catchLabels.forEach(label -> {
            finalGenerator.labelPackses.forEach(labelPacks -> {
                methodVisitor.visitTryCatchBlock(labelPacks.firstLabel, labelPacks.lastLabel, label.firstLabel, label.type.getInternalName());
            });
        });

        methodVisitor.visitLabel(lCatchBlockEnd);

    }


    private class ReturnStatementAlterer extends StatementGeneratorFilter {

        private AtomicReference<Label> label = new AtomicReference<>();
        private final List<LabelPacks> labelPackses;


        protected ReturnStatementAlterer(Label label, StatementGenerator parent, StatementGenerator next, Scope scope) {
            super(parent, next, scope);
            this.label.set(label);
            labelPackses = new ArrayList<>();
        }

        private ReturnStatementAlterer(List<LabelPacks> labelPackses, AtomicReference<Label> label, StatementGenerator parent, StatementGenerator next, Scope scope) {
            super(parent, next, scope);
            this.label = label;
            this.labelPackses = labelPackses;

        }


        @Override
        public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
            returnStatement.getExpression().accept(generator);
            Label second = new Label();
            methodVisitor.visitLabel(second);
            labelPackses.add(new LabelPacks(label.get(), second));

            methodVisitor.visitInsn(returnStatement.getExpression().getType().getReturnOpcode());

            Label first = new Label();
            methodVisitor.visitLabel(first);
            label.set(first);
        }

        public StatementGenerator copy(StatementGenerator parent) {
            return new ReturnStatementAlterer(labelPackses, label, parent, this.next, getScope());
        }
    }

    private static class LabelPacks {
        Label firstLabel;
        Label lastLabel;

        public LabelPacks(Label firstLabel, Label lastLabel) {
            this.firstLabel = firstLabel;
            this.lastLabel = lastLabel;
        }
    }

    private static class CatchPack {
        Label firstLabel;
        Type type;

        public CatchPack(Label firstLabel, Type type) {
            this.firstLabel = firstLabel;
            this.type = type;
        }
    }

}
