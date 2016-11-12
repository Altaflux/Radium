package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.node.statement.TryCatchStatement;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.collections4.ListUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.*;

public class TryCatchStatementGenerator {

    private final MethodVisitor methodVisitor;

    public TryCatchStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(TryCatchStatement tryCatchStatement, StatementGenerator statementGenerator) {

        Label lTryBlockStart = new Label();
        Label lTryBlockEnd = new Label();
        Label lCatchBlockEnd = new Label();

        Block finalBlock = tryCatchStatement.getFinallyBlock().orElse(null);
        ReturnStatementAlterer finalGenerator = new ReturnStatementAlterer(finalBlock, lTryBlockStart, null, statementGenerator, statementGenerator.getScope());


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

            List<LabelPacks> labelPackses = new ArrayList<>();

            if (hasFinally) {
                ReturnStatementAlterer catchGenerator = new ReturnStatementAlterer(finalBlock, catchStartLabel, null, statementGenerator, statementGenerator.getScope());
                catchBlock.getBlock().accept(catchGenerator);
                labelPackses.addAll(catchGenerator.labelPackses);

            } else {
                catchBlock.getBlock().accept(statementGenerator);
            }

            if (!catchBlock.isReturnComplete()) {
                methodVisitor.visitJumpInsn(GOTO, lCatchBlockEnd);
            }
            return new CatchPack(catchStartLabel, catchBlock.getParameter().getType(), labelPackses);
        }).collect(Collectors.toList());


        catchLabels.forEach(label -> {
            finalGenerator.labelPackses.forEach(labelPacks -> {
                methodVisitor.visitTryCatchBlock(labelPacks.firstLabel, labelPacks.lastLabel, label.firstLabel, label.type.getInternalName());
            });
        });


        if (hasFinally) {
            Label finalLabel = new Label();

            methodVisitor.visitLabel(finalLabel);
            Block finallyBlock = tryCatchStatement.getFinallyBlock().get();
            Scope catchBlockScope = finallyBlock.getScope();
            catchBlockScope.addLocalVariable(new LocalVariable("$$Throw", new JavaClassType("java.lang.Throwable"), true));
            methodVisitor.visitVarInsn(ASTORE, catchBlockScope.getLocalVariableIndex("$$Throw"));

            finallyBlock.accept(statementGenerator);

            methodVisitor.visitVarInsn(ALOAD, catchBlockScope.getLocalVariableIndex("$$Throw"));
            methodVisitor.visitInsn(ATHROW);

            List<LabelPacks> catchLabelList = catchLabels.stream().map(catchPack -> catchPack.labelPackses).flatMap(Collection::stream).collect(Collectors.toList());

            ListUtils.sum(catchLabelList, finalGenerator.labelPackses).forEach(labelPacks -> {
                methodVisitor.visitTryCatchBlock(labelPacks.firstLabel, labelPacks.lastLabel, finalLabel, null);
            });

        }

        methodVisitor.visitLabel(lCatchBlockEnd);

    }


    private class ReturnStatementAlterer extends StatementGeneratorFilter {

        private AtomicReference<Label> label = new AtomicReference<>();
        private final List<LabelPacks> labelPackses;
        private final Block finalBlock;
        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        protected ReturnStatementAlterer(Block finalBlock, Label label, StatementGenerator parent, StatementGenerator next, Scope scope) {
            super(parent, next, scope);
            this.label.set(label);
            labelPackses = new ArrayList<>();
            this.finalBlock = finalBlock;
        }

        private ReturnStatementAlterer(Block finalBlock, List<LabelPacks> labelPackses, AtomicReference<Label> label, StatementGenerator parent, StatementGenerator next, Scope scope) {
            super(parent, next, scope);
            this.label = label;
            this.labelPackses = labelPackses;
            this.finalBlock = finalBlock;

        }


        @Override
        public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
            returnStatement.getExpression().accept(generator);
            Label second = new Label();
            methodVisitor.visitLabel(second);
            labelPackses.add(new LabelPacks(label.get(), second));

            if (finalBlock != null) {
                String varName = "$$Return" + atomicInteger.incrementAndGet();
                getScope().addLocalVariable(new LocalVariable(varName, returnStatement.getExpression().getType()));
                methodVisitor.visitVarInsn(returnStatement.getExpression().getType().getStoreVariableOpcode(), getScope().getLocalVariableIndex(varName));

                StatementGeneratorFilter filter = new StatementGeneratorFilter(null, next, getScope());
                finalBlock.accept(filter);

                if (!finalBlock.isReturnComplete()) {
                    methodVisitor.visitVarInsn(returnStatement.getExpression().getType().getLoadVariableOpcode(), getScope().getLocalVariableIndex(varName));
                    methodVisitor.visitInsn(returnStatement.getExpression().getType().getReturnOpcode());
                }

            } else {
                methodVisitor.visitInsn(returnStatement.getExpression().getType().getReturnOpcode());
            }

            Label first = new Label();
            methodVisitor.visitLabel(first);
            label.set(first);
        }

        public StatementGenerator copy(StatementGenerator parent) {
            return new ReturnStatementAlterer(finalBlock, labelPackses, label, parent, this.next, getScope());
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
        List<LabelPacks> labelPackses;

        public CatchPack(Label firstLabel, Type type, List<LabelPacks> labelPackses) {
            this.firstLabel = firstLabel;
            this.type = type;
            this.labelPackses = labelPackses;
        }
    }

}
