package com.kubadziworski.bytecodegeneration.statement;

import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.node.statement.TryCatchStatement;
import com.kubadziworski.domain.scope.LocalVariable;
import com.kubadziworski.domain.scope.Scope;
import com.kubadziworski.domain.type.ClassTypeFactory;
import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.objectweb.asm.Opcodes.*;

public class TryCatchStatementGenerator {

    private final MethodVisitor methodVisitor;

    private static final String FINALLY_THROWABLE_NAME = "$$Throw";

    public TryCatchStatementGenerator(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public void generate(TryCatchStatement tryCatchStatement, StatementGenerator statementGenerator) {

        Label startOfTryBlock = new Label();
        Label endOfTryBlock = new Label();
        Label fullEndOfTryCatchStatementLabel = new Label();
        methodVisitor.visitLabel(startOfTryBlock);

        Optional<Block> finalBlock = tryCatchStatement.getFinallyBlock();
        TryCatchFilter finalGenerator = new TryCatchFilter(finalBlock.orElse(null), startOfTryBlock, null, statementGenerator, statementGenerator.getScope());

        Statement tryExpression = tryCatchStatement.getStatement();
        tryExpression.accept(finalGenerator);

        if (!tryExpression.isReturnComplete()) {
            methodVisitor.visitJumpInsn(GOTO, fullEndOfTryCatchStatementLabel);
        }
        methodVisitor.visitLabel(endOfTryBlock);

        if (finalGenerator.labelPacks.isEmpty()) {
            finalGenerator.labelPacks.add(new LabelPack(startOfTryBlock, endOfTryBlock));
        }

        List<CatchPack> catchLabels = tryCatchStatement.getCatchBlocks().stream()
                .map(catchBlock -> {
                    Label catchStartLabel = new Label();
                    methodVisitor.visitLabel(catchStartLabel);
                    Scope catchBlockScope = catchBlock.getBlock().getScope();
                    methodVisitor.visitVarInsn(ASTORE, catchBlockScope.getLocalVariableIndex(catchBlock.getParameter().getName()));

                    List<LabelPack> labelPacks = new ArrayList<>();

                    if (finalBlock.isPresent()) {
                        TryCatchFilter catchGenerator = new TryCatchFilter(finalBlock.get(), catchStartLabel, null, statementGenerator, statementGenerator.getScope());
                        catchBlock.getBlock().accept(catchGenerator);
                        labelPacks.addAll(catchGenerator.labelPacks);
                    } else {
                        catchBlock.getBlock().accept(statementGenerator);
                    }

                    if (!catchBlock.isReturnComplete()) {
                        methodVisitor.visitJumpInsn(GOTO, fullEndOfTryCatchStatementLabel);
                    }
                    return new CatchPack(catchStartLabel, catchBlock.getParameter().getType(), labelPacks);
                }).collect(Collectors.toList());


        catchLabels.forEach(label -> finalGenerator.labelPacks
                .forEach(labelPack ->
                        methodVisitor.visitTryCatchBlock(labelPack.firstLabel, labelPack.lastLabel,
                                label.firstLabel, label.type.getInternalName())));


        if (finalBlock.isPresent()) {
            Label finalLabel = new Label();
            methodVisitor.visitLabel(finalLabel);

            Block finallyBlock = finalBlock.get();

            Scope catchBlockScope = finallyBlock.getScope();
            catchBlockScope.addLocalVariable(new LocalVariable(FINALLY_THROWABLE_NAME, ClassTypeFactory.createClassType("java.lang.Throwable"), true));
            methodVisitor.visitVarInsn(ASTORE, catchBlockScope.getLocalVariableIndex(FINALLY_THROWABLE_NAME));
            finallyBlock.accept(statementGenerator);

            if (!finallyBlock.isReturnComplete()) {
                methodVisitor.visitVarInsn(ALOAD, catchBlockScope.getLocalVariableIndex(FINALLY_THROWABLE_NAME));
                methodVisitor.visitInsn(ATHROW);
            }

            Stream.concat(catchLabels.stream().map(catchPack -> catchPack.labelPacks).flatMap(Collection::stream), finalGenerator.labelPacks.stream())
                    .forEach(labelPack -> methodVisitor.visitTryCatchBlock(labelPack.firstLabel, labelPack.lastLabel, finalLabel, null));

        }
        methodVisitor.visitLabel(fullEndOfTryCatchStatementLabel);
    }


    private class TryCatchFilter extends StatementGeneratorFilter {

        private AtomicReference<Label> startLabel = new AtomicReference<>();
        private final List<LabelPack> labelPacks;
        private final Block finalBlock;
        private final AtomicInteger atomicInteger = new AtomicInteger(0);

        TryCatchFilter(Block finalBlock, Label startLabel, StatementGenerator parent, StatementGenerator next, Scope scope) {
            super(parent, next, scope);
            this.startLabel.set(startLabel);
            labelPacks = new ArrayList<>();
            this.finalBlock = finalBlock;
        }

        private TryCatchFilter(Block finalBlock, List<LabelPack> labelPacks, AtomicReference<Label> startLabel,
                               StatementGenerator parent, StatementGenerator next, Scope scope) {
            super(parent, next, scope);
            this.startLabel = startLabel;
            this.labelPacks = labelPacks;
            this.finalBlock = finalBlock;
        }

        @Override
        public void generate(ReturnStatement returnStatement, StatementGenerator generator) {
            returnStatement.getExpression().accept(generator);

            Label endOfReturnLabel = new Label();
            methodVisitor.visitLabel(endOfReturnLabel);
            labelPacks.add(new LabelPack(startLabel.get(), endOfReturnLabel));

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

            Label nextReturnStartLabel = new Label();
            methodVisitor.visitLabel(nextReturnStartLabel);
            startLabel.set(nextReturnStartLabel);
        }

        public StatementGenerator copy(StatementGenerator parent) {
            return new TryCatchFilter(finalBlock, labelPacks, startLabel, parent, this.next, getScope());
        }
    }

    private static class LabelPack {
        Label firstLabel;
        Label lastLabel;

        LabelPack(Label firstLabel, Label lastLabel) {
            this.firstLabel = firstLabel;
            this.lastLabel = lastLabel;
        }
    }

    private static class CatchPack {
        Label firstLabel;
        Type type;
        List<LabelPack> labelPacks;

        CatchPack(Label firstLabel, Type type, List<LabelPack> labelPacks) {
            this.firstLabel = firstLabel;
            this.type = type;
            this.labelPacks = labelPacks;
        }
    }

}
