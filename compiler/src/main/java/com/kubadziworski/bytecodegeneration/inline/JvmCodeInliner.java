package com.kubadziworski.bytecodegeneration.inline;


import com.kubadziworski.domain.node.expression.FunctionCall;
import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.CompilationException;
import com.kubadziworski.util.DescriptorFactory;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Optional;

public class JvmCodeInliner implements CodeInliner {

    public static CodeInliner INSTANCE = new JvmCodeInliner();

    private JvmCodeInliner() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inlineMethod(Type currentClass, InstructionAdapter visitor, FunctionCall functionCall) {

        String methodDescriptor = DescriptorFactory.getMethodDescriptor(functionCall.getSignature());
        String newClass = currentClass.getName();
        String oldClass = functionCall.getSignature().getOwner().getName();
        String ownerDescriptor = functionCall.getSignature().getOwner().getAsmType().getInternalName();
        ClassNode classNode = ((JavaClassType) functionCall.getSignature().getOwner()).getClassNode(false);
        Optional<MethodNode> methodNodeOp = ((List<MethodNode>) classNode.methods).stream()
                .filter(o -> (o.desc.equals(methodDescriptor)))
                .filter(methodNode -> methodNode.name.equals(functionCall.getName()))
                .findFirst();

        MethodNode methodNode = methodNodeOp
                .orElseThrow(() -> new CompilationException("Could not find method: " + functionCall.getName()));

        MethodCallInliner methodCallInliner = new MethodCallInliner(methodNode.access, methodNode.desc, visitor, methodNode, oldClass, ownerDescriptor, newClass);

        int callOpCode = functionCall.getInvokeOpcode();
        methodCallInliner.visitMethodInsn(callOpCode, ownerDescriptor, functionCall.getName(), methodDescriptor, false);
    }
}
