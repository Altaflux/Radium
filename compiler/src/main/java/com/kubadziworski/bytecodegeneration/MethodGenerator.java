package com.kubadziworski.bytecodegeneration;

import com.kubadziworski.bytecodegeneration.intrinsics.IntrinsicMethodCaller;
import com.kubadziworski.bytecodegeneration.statement.StatementGenerator;
import com.kubadziworski.bytecodegeneration.statement.StatementGeneratorFilter;
import com.kubadziworski.bytecodegeneration.util.ModifierTransformer;
import com.kubadziworski.bytecodegeneration.util.PropertyAccessorsGenerator;
import com.kubadziworski.bytecodegeneration.util.defaultmethod.DefaultMethodHandler;
import com.kubadziworski.bytecodegeneration.util.defaultmethod.DefaultMethodStatementFilter;
import com.kubadziworski.domain.Constructor;
import com.kubadziworski.domain.Function;
import com.kubadziworski.domain.Modifier;
import com.kubadziworski.domain.node.expression.EmptyExpression;
import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.node.statement.Block;
import com.kubadziworski.domain.node.statement.FieldAssignment;
import com.kubadziworski.domain.node.statement.ReturnStatement;
import com.kubadziworski.domain.node.statement.Statement;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionScope;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.stream.IntStream;


public class MethodGenerator {

    private final ClassVisitor cv;
    private final DefaultMethodHandler defaultMethodHandler = new DefaultMethodHandler();

    public MethodGenerator(ClassVisitor cv) {
        this.cv = cv;
    }

    public void generate(Function function) {
        String name = function.getName();
        String description = DescriptorFactory.getMethodDescriptor(function);
        Block block = (Block) function.getRootStatement();
        FunctionScope scope = block.getScope();

        int mod = ModifierTransformer.transform(function.getModifiers());
        MethodVisitor mvs = cv.visitMethod(mod, name, description, null, null);
        InstructionAdapter mv = new InstructionAdapter(mvs);
        generateInlineAnnotation(function, mv);
        generateMutabilityAnnotations(function, mv);

        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        DefaultMethodStatementFilter defaultMethodStatementFilter = new DefaultMethodStatementFilter(mv, null, statementScopeGenerator, scope);
        IntrinsicMethodCaller intrinsicMethodCaller = new IntrinsicMethodCaller(mv, null, defaultMethodStatementFilter, scope);


        block.accept(intrinsicMethodCaller);
        appendReturnIfNotExists(function, block, statementScopeGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        defaultMethodHandler.createSyntheticForFunction(function, cv);
    }


    public void generatePropertyAccessor(Function function, Field field) {
        String name = function.getName();
        String description = DescriptorFactory.getMethodDescriptor(function);
        Block block = (Block) function.getRootStatement();
        FunctionScope scope = block.getScope();
        MethodVisitor mvs = cv.visitMethod(ModifierTransformer.transform(function.getModifiers()), name, description, null, null);
        InstructionAdapter mv = new InstructionAdapter(mvs);
        generateMutabilityAnnotations(function, mv);

        mv.visitCode();
        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        StatementGenerator filteringGenerator = new AccessorInterceptorFilter(field, mv, null, statementScopeGenerator, scope);

        block.accept(filteringGenerator);
        appendReturnIfNotExists(function, block, filteringGenerator);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();
    }

    public void generate(Constructor constructor) {
        Block block = (Block) constructor.getRootStatement();
        FunctionScope scope = block.getScope();
        String description = DescriptorFactory.getMethodDescriptor(constructor);
        MethodVisitor mvs = cv.visitMethod(ModifierTransformer.transform(constructor.getModifiers()), "<init>", description, null, null);
        InstructionAdapter mv = new InstructionAdapter(mvs);
        generateMutabilityAnnotations(constructor, mv);
        mv.visitCode();

        StatementGenerator statementScopeGenerator = new StatementGeneratorFilter(mv, scope);
        DefaultMethodStatementFilter defaultMethodStatementFilter = new DefaultMethodStatementFilter(mv, null, statementScopeGenerator, scope);
        IntrinsicMethodCaller intrinsicMethodCaller = new IntrinsicMethodCaller(mv, null, defaultMethodStatementFilter, scope);

        block.accept(intrinsicMethodCaller);
        appendReturnIfNotExists(constructor, block, intrinsicMethodCaller);
        mv.visitMaxs(-1, -1);
        mv.visitEnd();

        defaultMethodHandler.createSyntheticForConstructor(constructor, cv, this);
    }

    private void generateInlineAnnotation(Function function, InstructionAdapter mv) {
        if (function.getModifiers().contains(Modifier.INLINE)) {
            AnnotationVisitor av0 = mv.visitAnnotation("Lradium/internal/InlineOnly;", true);
            av0.visitEnd();
        }
    }

    private void generateMutabilityAnnotations(Function function, InstructionAdapter mv) {
        if (function.getReturnType().isNullable().equals(Type.Nullability.NULLABLE)) {
            AnnotationVisitor av0 = mv.visitAnnotation("Lradium/annotations/Nullable;", false);
            av0.visitEnd();
        } else {
            AnnotationVisitor av0 = mv.visitAnnotation("Lradium/annotations/NotNull;", false);
            av0.visitEnd();
        }

        IntStream.range(0, function.getParameters().size()).forEach(index -> {
            Parameter parameter = function.getParameters().get(index);
            if (parameter.getType().isNullable().equals(Type.Nullability.NULLABLE) || parameter.getDefaultValue().isPresent()) {
                AnnotationVisitor av0 = mv.visitParameterAnnotation(index, "Lradium/annotations/Nullable;", false);
                av0.visitEnd();
            } else {
                AnnotationVisitor av0 = mv.visitParameterAnnotation(index, "Lradium/annotations/NotNull;", false);
                av0.visitEnd();
            }
        });

    }

    private void appendReturnIfNotExists(Function function, Block block, StatementGenerator statementScopeGenerator) {
        boolean isLastStatementReturn = false;
        if (!block.getStatements().isEmpty()) {
            Statement lastStatement = block.getStatements().get(block.getStatements().size() - 1);
            isLastStatementReturn = lastStatement.isReturnComplete();
        }
        if (!isLastStatementReturn) {
            EmptyExpression emptyExpression = new EmptyExpression(function.getReturnType());
            ReturnStatement returnStatement = new ReturnStatement(emptyExpression);
            returnStatement.accept(statementScopeGenerator);
        }
    }

    private static class AccessorInterceptorFilter extends StatementGeneratorFilter {
        private final Field field;
        private final InstructionAdapter adapter;

        private AccessorInterceptorFilter(Field field, InstructionAdapter adapter, StatementGenerator parent, StatementGenerator next, FunctionScope scope) {
            super(parent, next, scope);
            this.field = field;
            this.adapter = adapter;
        }

        @Override
        public void generate(FieldAssignment assignment, StatementGenerator generator) {
            if (field.equals(assignment.getField())) {
                PropertyAccessorsGenerator.generateNoPropertyTransformation(assignment, generator, adapter);
                next.generate(new EmptyExpression(assignment.getNodeData(), assignment.getField().getType()));
            } else {
                next.generate(assignment, generator);
            }
        }

        @Override
        public void generate(FieldReference fieldReference, StatementGenerator generator) {
            if (field.equals(fieldReference.getField())) {
                PropertyAccessorsGenerator.generateNoPropertyTransformation(fieldReference, generator, adapter);
                next.generate(new EmptyExpression(fieldReference.getNodeData(), fieldReference.getType()));
            } else {
                next.generate(fieldReference, generator);
            }
        }

        public StatementGenerator copy(StatementGenerator parent) {
            return new AccessorInterceptorFilter(field, adapter, parent, this.next, getScope());
        }
    }
}
