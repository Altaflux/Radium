package com.kubadziworski.bytecodegeneration.inline;

import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.exception.CompilationException;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;
import java.util.Optional;

public class JvmCodeInliner implements CodeInliner {

    public static CodeInliner INSTANCE = new JvmCodeInliner();

    private JvmCodeInliner() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public MethodNode getMethodNode(Type owner, String name, String desc) {
        ClassNode classNode = ((JavaClassType) owner).getClassNode(false);
        Optional<MethodNode> methodNodeOp = ((List<MethodNode>) classNode.methods).stream()
                .filter(o -> (o.desc.equals(desc)))
                .filter(methodNode -> methodNode.name.equals(name))
                .findFirst();

        return methodNodeOp
                .orElseThrow(() -> new CompilationException("Could not find method: " + name));
    }
}
